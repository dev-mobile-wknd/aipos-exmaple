import AIPosSDK
import Foundation

/// Contoh penyaran produk dari percakapan sales–pelanggan (`AdvisorClient`).
///
/// Dirakit lewat `AdvisorClient` — **bukan** `AIPosSDK` — supaya jelas bahwa host app
/// yang cuma butuh fitur AI ini tidak perlu menyeret modul pembayaran sama sekali. Tidak
/// ada `.ios()` di sini: penyaran murni Kotlin, tidak menyentuh apa pun yang khusus
/// platform.
@MainActor
final class AdvisorModel: ObservableObject {

    private let client: AdvisorClient
    private var subscriptions: [FlowSubscription] = []

    @Published private(set) var suggestions: [ProductSuggestion] = []
    @Published private(set) var buyerIntent: String = ""
    @Published private(set) var state: AdvisorState = AdvisorState.Idle()
    @Published private(set) var transcript: [String] = []
    @Published private(set) var error: String?

    init() {
        // Kunci OpenAI dibaca dari environment variable — set lewat Xcode
        // (Product ▸ Scheme ▸ Edit Scheme… ▸ Run ▸ Arguments ▸ Environment Variables)
        // atau `LLM_API_KEY=sk-... open Advisor.xcodeproj`. Tidak ikut ter-commit karena
        // tidak ditulis di file apa pun.
        let apiKey = ProcessInfo.processInfo.environment["LLM_API_KEY"] ?? ""

        client = AdvisorClient.Builder()
            .llmApiKey(key: apiKey)
            .productCatalog(source: MutableProductCatalog(initial: demoCatalog))
            .build()

        subscriptions.append(observe(client.observeSuggestions(), as: [ProductSuggestion].self) { [weak self] in
            self?.suggestions = $0
        })
        subscriptions.append(observe(client.observeBuyerIntent(), as: String.self) { [weak self] in
            self?.buyerIntent = $0
        })
        subscriptions.append(observe(client.observeAdvisorState(), as: AdvisorState.self) { [weak self] in
            self?.state = $0
        })
        subscriptions.append(observe(client.observeTranscript(), as: [String].self) { [weak self] in
            self?.transcript = $0
        })
        // `observeAdvisorError()` bertipe `Flow<String?>` di Kotlin. `observe(...)`
        // men-cast ke `String` non-optional (bukan `String?`) mengikuti pola yang sudah
        // berjalan di `sample-ios` — konsekuensinya nilai `null` (tidak ada error)
        // diam-diam diabaikan, sehingga `error` hanya pernah terisi, tidak pernah
        // otomatis kembali ke `nil`. Cukup untuk demo; aplikasi produksi yang perlu
        // membersihkan pesan error sebaiknya mereset `error` sendiri saat mengirim
        // transkrip baru.
        subscriptions.append(observe(client.observeAdvisorError(), as: String.self) { [weak self] in
            self?.error = $0
        })
    }

    /// Kirim satu kalimat pelanggan yang sudah final.
    ///
    /// Di aplikasi sungguhan, ini dipanggil dari hasil speech-to-text perangkat — bukan
    /// kotak teks — setiap kali satu kalimat pelanggan selesai dikenali.
    func send(_ text: String) {
        client.pushTranscript(text: text)
    }

    var stateText: String {
        if state is AdvisorState.Idle { return "Menunggu percakapan" }
        if state is AdvisorState.Thinking { return "Menganalisis..." }
        if state is AdvisorState.Suggesting { return "Ada saran" }
        return ""
    }

    deinit {
        subscriptions.forEach { $0.cancel() }
    }
}
