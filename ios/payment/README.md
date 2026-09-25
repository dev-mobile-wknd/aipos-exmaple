# Contoh: Pembayaran (`PaymentClient` + `OnlinePaymentClient`)

Padanan iOS dari [`example/android/payment`](../../android/payment). Dua tab:

1. **Tap Kartu** — `PaymentClient`. **Selalu berakhir gagal saat ini**: iOS belum
   mendukung Tap to Pay (perlu framework `ProximityReader` + entitlement Apple yang
   belum ada di SDK ini). Tab ini tetap ditampilkan apa adanya — memanggil
   `processPayment()` sungguhan dan menampilkan `PaymentState.Failed` yang dikembalikan
   — supaya batasannya terlihat, bukan disembunyikan. Lihat
   [`PaymentModels.swift`](Sources/PaymentModels.swift).
2. **Online (VA/QRIS)** — `OnlinePaymentClient`. Berjalan penuh: `startSession()`
   menghasilkan URL, dimuat di `WKWebView` lewat
   [`PaymentWebView.swift`](Sources/PaymentWebView.swift), status dibaca dari
   `client.status` setelah `attachPaymentPageProbe(probe:)`.

Keduanya dirakit lewat client masing-masing — **bukan** `AIPosSDK` — supaya jelas bahwa
host app yang cuma jual dan bayar tidak perlu menyeret AI agent maupun penyaran produk
sama sekali.

## Menjalankan

```bash
brew install xcodegen   # sekali saja
xcodegen generate
open Payment.xcodeproj
```

Tidak butuh kredensial apa pun — `swift-aipos-sdk` publik, dan pembayaran online
memakai backend beta bersama milik SDK secara bawaan.
