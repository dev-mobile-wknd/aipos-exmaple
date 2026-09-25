# Contoh: Pembayaran (`aipos-payment` + `aipos-payment-online`)

Dua cara menerima pembayaran, masing-masing di tab terpisah:

1. **Tap Kartu** — `PaymentClient` dari `aipos-payment`. Tambah produk contoh ke
   keranjang lalu `processPayment()`, mengalirkan `PaymentState` (menunggu tap →
   diproses → sukses/gagal). Lihat [`MainActivity.kt`](app/src/main/kotlin/com/weekendinc/example/payment/MainActivity.kt).
2. **Online (VA/QRIS)** — `OnlinePaymentClient` dari `aipos-payment-online`.
   `startSession()` menghasilkan URL, dimuat di `WebView`, lalu status pembayaran
   dibaca dari `client.status` setelah `attachPaymentPageProbe(webView.paymentPageProbe())`.

Keduanya dirakit lewat client masing-masing — **bukan** `AIPosSDK` — supaya jelas
bahwa host app yang cuma jual dan bayar tidak perlu menyeret AI agent maupun
penyaran produk sama sekali.

## Menjalankan

```bash
./gradlew :app:assembleDebug
```

Tanpa kredensial MineSec di `~/.gradle/gradle.properties` (lihat `../README.md`),
tap kartu otomatis jatuh ke `SimulatorPaymentGateway` — selalu disetujui, tanpa
menyentuh perangkat keras. Kredensial MineSec tetap **wajib ada** agar dependency-nya
bisa di-resolve sama sekali (401 saat build kalau tidak ada), terlepas dari simulator
atau tidak.

Pembayaran online memakai backend beta bersama milik SDK secara bawaan — tidak perlu
kredensial apa pun untuk mencobanya.
