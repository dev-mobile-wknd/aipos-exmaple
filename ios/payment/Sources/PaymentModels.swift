import AIPosSDK
import Foundation

/// Contoh dua cara menerima pembayaran, dirakit lewat `PaymentClient`/`OnlinePaymentClient`
/// masing-masing — **bukan** lewat `AIPosSDK` — supaya jelas bahwa host app yang hanya
/// butuh pembayaran tidak perlu menyeret AI agent maupun penyaran produk sama sekali.

/// Tap kartu di terminal (`PaymentClient`).
///
/// **Selalu berakhir gagal di iOS saat ini** — lihat `IosPaymentGateway` di sisi Kotlin.
/// Jalur ini tetap disediakan apa adanya supaya perbedaannya terlihat, bukan disembunyikan.
@MainActor
final class ContactlessPaymentModel: ObservableObject {

    private let client: PaymentClient
    private var subscription: FlowSubscription?

    @Published private(set) var state: PaymentState = PaymentState.Idle()

    init() {
        client = PaymentClient.Builder()
            .merchantInfo(info: demoMerchant)
            .productCatalog(source: MutableProductCatalog(initial: [demoProduct]))
            .ios()
            .build()
    }

    var statusText: String {
        if state is PaymentState.Idle {
            return "Belum ada pembayaran berjalan."
        } else if let waiting = state as? PaymentState.WaitingTap {
            return waiting.message
        } else if let processing = state as? PaymentState.Processing {
            return processing.message
        } else if let success = state as? PaymentState.Success {
            let amount = IosDomainFactoryKt.formatMoneyCents(cents: success.amount)
            return "Lunas — transaksi \(success.transactionId), \(amount)"
        } else if let failed = state as? PaymentState.Failed {
            return "Gagal: \(failed.errorMessage)"
        }
        return ""
    }

    var isTerminal: Bool { state.isTerminal }

    func payNow() {
        Task {
            _ = try? await client.addToCart(productId: demoProduct.id, quantity: 1)
            subscription?.cancel()
            subscription = observe(client.processPayment(), as: PaymentState.self) { [weak self] value in
                self?.state = value
            }
        }
    }

    func newTransaction() {
        Task {
            try? await client.clearCart()
            state = PaymentState.Idle()
        }
    }
}

/// Tautan pembayaran online — Virtual Account / QRIS via WebView (`OnlinePaymentClient`).
///
/// Berjalan penuh di iOS: seluruh alurnya hanya panggilan HTTP biasa, tidak menyentuh
/// perangkat keras apa pun.
@MainActor
final class OnlinePaymentModel: ObservableObject {

    let client: OnlinePaymentClient
    private var statusSubscription: FlowSubscription?

    @Published private(set) var status: WebPaymentStatus = WebPaymentStatus.Idle()
    @Published private(set) var session: WebPaymentSession?
    @Published var errorMessage: String?

    init() {
        client = OnlinePaymentClient.Builder()
            .merchantInfo(info: demoMerchant)
            .productCatalog(source: MutableProductCatalog(initial: [demoProduct]))
            .ios()
            .build()
        // `.config(...)` sengaja tidak dipanggil: tanpa itu SDK memakai backend beta
        // bersama milik AI POS SDK, cukup untuk demo tanpa backend merchant sendiri.

        statusSubscription = observe(client.status, as: WebPaymentStatus.self) { [weak self] value in
            self?.status = value
        }
    }

    var statusText: String {
        if status is WebPaymentStatus.Idle {
            return "Belum ada pembayaran berjalan."
        } else if status is WebPaymentStatus.ChoosingMethod {
            return "Menunggu pelanggan memilih kanal pembayaran..."
        } else if let pending = status as? WebPaymentStatus.Pending {
            return "Menunggu dana masuk (transaksi \(pending.transactionId ?? "-"))..."
        } else if let success = status as? WebPaymentStatus.Success {
            return "Lunas — transaksi \(success.transactionId ?? "-")"
        } else if let failed = status as? WebPaymentStatus.Failed {
            return "Gagal: \(failed.rawStatus ?? "tidak diketahui")"
        } else if status is WebPaymentStatus.Cancelled {
            return "Dibatalkan."
        }
        return "Status belum dikenal."
    }

    var isFinal: Bool { status.isFinal }

    func createLink() {
        Task {
            _ = try? await client.addToCart(productId: demoProduct.id, quantity: 1)

            let opened = try? await client.startSession(note: nil)
            guard let success = opened as? PosResultSuccess<WebPaymentSession>,
                  let session = success.data
            else {
                errorMessage = (opened as? PosResultFailure)?.message ?? "Gagal membuka sesi pembayaran."
                return
            }
            self.session = session
        }
    }

    func newTransaction() {
        Task {
            client.resetSession()
            try? await client.clearCart()
            session = nil
        }
    }
}
