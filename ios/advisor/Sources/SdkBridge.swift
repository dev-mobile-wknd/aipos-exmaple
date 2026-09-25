import AIPosSDK
import Foundation

/// Penghalus sudut-sudut tajam yang muncul saat SDK Kotlin dilihat dari Swift.
///
/// `Money` sudah luruh jadi `Int64` berisi cent saat menyeberang dari Kotlin ke
/// Objective-C — `Money.format()` hilang bersamanya. `IosDomainFactoryKt.formatMoneyCents`
/// mengembalikannya, supaya format harga tetap satu sumber dengan Android.
extension Product {
    /// Harga satuan siap tampil, misal `"Rp 25.000"`.
    var priceText: String { IosDomainFactoryKt.formatMoneyCents(cents: price) }
}

/// Amati sebuah `Flow` Kotlin dan serahkan nilainya yang sudah diturunkan tipenya.
///
/// `Flow` tidak punya padanan native di Swift; dijembatani lewat
/// `FlowSubscriptionKt.subscribe`, dibungkus tipis di sini. `assumeIsolated` bukan
/// tebakan: sisi Kotlin menjalankan `onEach` di `Dispatchers.Main`.
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
