import AIPosSDK
import Foundation

/// Penghalus sudut-sudut tajam yang muncul saat SDK Kotlin dilihat dari Swift.
///
/// Dua hal tidak ikut menyeberang dari Kotlin ke Objective-C:
/// - **value class.** `Money` sudah luruh jadi `Int64` berisi cent, dan `ProductId` jadi
///   `Any` tanpa bentuk. `Money.format()` karena itu hilang — dikembalikan lewat
///   `IosDomainFactoryKt.formatMoneyCents`, bukan ditiru ulang di sini, supaya format
///   harga tetap satu sumber dengan Android.
/// - **`Flow`.** Tidak punya padanan native di Swift; dijembatani lewat
///   `FlowSubscriptionKt.subscribe`, yang dibungkus tipis oleh `observe(...)` di bawah.
extension Product {
    /// Harga satuan siap tampil, misal `"Rp 25.000"`.
    var priceText: String { IosDomainFactoryKt.formatMoneyCents(cents: price) }
}

extension WebPaymentSession {
    /// Nominal yang ditagihkan, siap tampil.
    var amountText: String { IosDomainFactoryKt.formatMoneyCents(cents: amount) }
}

/// Amati sebuah `Flow` Kotlin dan serahkan nilainya yang sudah diturunkan tipenya.
///
/// `assumeIsolated` bukan tebakan: sisi Kotlin menjalankan `onEach` di
/// `Dispatchers.Main`, yang di iOS berarti antrian utama.
@discardableResult
func observe<T>(
    _ flow: any Kotlinx_coroutines_coreFlow,
    as type: T.Type = T.self,
    onEach: @escaping @MainActor (T) -> Void
) -> FlowSubscription {
    FlowSubscriptionKt.subscribe(flow) { value in
        guard let typed = value as? T else { return }
        MainActor.assumeIsolated { onEach(typed) }
    }
}
