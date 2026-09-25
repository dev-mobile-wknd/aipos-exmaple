import SwiftUI

/// Contoh dua cara menerima pembayaran: tap kartu di terminal (`PaymentClient`) dan
/// tautan Virtual Account / QRIS di WebView (`OnlinePaymentClient`).
///
/// Berbeda dari `sample-ios` di repo utama yang perlu `PosApplication` untuk menyalakan
/// terminal pembayaran lebih dulu, di sini tidak ada yang perlu disiapkan sebelum SDK
/// dirakit — dan tap kartu memang belum didukung di iOS (lihat README).
@main
struct PaymentExampleApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
