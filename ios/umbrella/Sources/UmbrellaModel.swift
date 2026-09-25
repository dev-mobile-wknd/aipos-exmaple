import AIPosSDK
import Foundation

/// Contoh umbrella `AIPosSDK`: satu titik masuk yang merakit AI agent kasir, penyaran
/// produk, keranjang, pembayaran terminal, dan pembayaran online sekaligus.
///
/// Bedanya dengan contoh domain `payment` dan `advisor`: di sini kasir tidak perlu
/// menekan "tambah ke keranjang" atau "bayar" secara manual sama sekali — cukup ketik
/// permintaan pelanggan dalam Bahasa Indonesia, dan agent memanggil tool yang tepat
/// sendiri lewat `sendMessage(message:)`. Jalur langsung tanpa AI (`addToCart`,
/// `processPayment`, dst.) tetap tersedia berdampingan — lihat contoh `payment` untuk
/// itu.
@MainActor
final class UmbrellaModel: ObservableObject {

    private let sdk: AIPosSDK
    private var subscriptions: [FlowSubscription] = []

    @Published private(set) var messages: [AgentMessage] = []
    @Published private(set) var isProcessing: Bool = false
    @Published private(set) var cart = Cart(items: [], discount: 0)
    @Published private(set) var paymentState: PaymentState = PaymentState.Idle()

    init() {
        // Kunci OpenAI dibaca dari environment variable — set lewat Xcode
        // (Product ▸ Scheme ▸ Edit Scheme… ▸ Run ▸ Arguments ▸ Environment Variables)
        // atau `LLM_API_KEY=sk-... open Umbrella.xcodeproj`. Tidak ikut ter-commit karena
        // tidak ditulis di file apa pun.
        let apiKey = ProcessInfo.processInfo.environment["LLM_API_KEY"] ?? ""

        sdk = AIPosSDK.Builder()
            .llmApiKey(key: apiKey)
            .merchantInfo(info: demoMerchant)
            .productCatalog(source: MutableProductCatalog(initial: demoCatalog))
            .ios()
            .build()
        // `.onlinePayment(...)` sengaja tidak dipanggil: tanpa itu SDK memakai backend
        // beta bersama, cukup untuk demo tanpa backend merchant sendiri.

        subscriptions.append(observe(sdk.observeMessages(), as: [AgentMessage].self) { [weak self] in
            self?.messages = $0
        })
        subscriptions.append(observe(sdk.observeProcessing(), as: Bool.self) { [weak self] in
            self?.isProcessing = $0
        })
        subscriptions.append(observe(sdk.observeCart(), as: Cart.self) { [weak self] in
            self?.cart = $0
        })
        subscriptions.append(observe(sdk.observePaymentState(), as: PaymentState.self) { [weak self] in
            self?.paymentState = $0
        })
    }

    func send(_ text: String) {
        Task { _ = try? await sdk.sendMessage(message: text) }
    }

    var paymentText: String {
        if paymentState is PaymentState.Idle { return "belum ada transaksi" }
        if let waiting = paymentState as? PaymentState.WaitingTap { return waiting.message }
        if let processing = paymentState as? PaymentState.Processing { return processing.message }
        if let success = paymentState as? PaymentState.Success { return "lunas (\(success.transactionId))" }
        if let failed = paymentState as? PaymentState.Failed { return "gagal — \(failed.errorMessage)" }
        return ""
    }

    deinit {
        subscriptions.forEach { $0.cancel() }
    }
}

extension AgentMessage {
    /// Baris siap tampil untuk satu entri percakapan.
    ///
    /// `AgentMessage` sealed class **tanpa** parameter generik, jadi subclass-nya tetap
    /// nested saat menyeberang ke Swift (`AgentMessage.UserMessage`, dst.) — beda dari
    /// `PosResult<T>` yang generik dan karena itu diratakan jadi `PosResultSuccess`/
    /// `PosResultFailure` tanpa nesting.
    var displayText: String {
        if let user = self as? AgentMessage.UserMessage { return "Kasir: \(user.content)" }
        if let assistant = self as? AgentMessage.AssistantMessage { return "Agent: \(assistant.content)" }
        if let tool = self as? AgentMessage.ToolCall { return "[tool] \(tool.toolName) — \(tool.summary)" }
        if let error = self as? AgentMessage.ErrorMessage { return "[error] \(error.message)" }
        return ""
    }
}
