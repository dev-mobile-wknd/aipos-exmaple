# Contoh: Umbrella (`aipos-sdk`)

Satu titik masuk `AIPosSDK` yang merakit seluruh fitur sekaligus: agent kasir AI,
penyaran produk, keranjang, pembayaran terminal, dan pembayaran online.

Layarnya satu kotak chat. Ketik permintaan pelanggan dalam Bahasa Indonesia —
misal *"saya mau kopi susu satu, bayar kartu"* — dan `sdk.sendMessage(...)`
menyerahkannya ke AI agent, yang memanggil tool pencarian produk, keranjang, dan
pembayaran sendiri. Status keranjang dan pembayaran ikut ditampilkan lewat
`observeCart()` dan `observePaymentState()`, supaya UI tetap konsisten dengan apa
yang dilakukan agent tanpa perlu menebak-nebak dari isi chat. Lihat
[`MainActivity.kt`](app/src/main/kotlin/com/weekendinc/example/umbrella/MainActivity.kt).

Jalur langsung tanpa AI (`addToCart`, `processPayment`, dst.) tetap ada di `AIPosSDK`
untuk kasus kasir menekan tombol produk secara manual — didemokan terpisah di
contoh [`payment/`](../payment) supaya tidak bercampur dengan alur AI di sini.

## Menjalankan

1. Salin `app/local.properties.example` → `app/local.properties`, isi `LLM_API_KEY`.
2. Isi `GITHUB_PACKAGES_LOGIN`/`GITHUB_PACKAGES_TOKEN` **dan**
   `MINESEC_REGISTRY_LOGIN`/`MINESEC_REGISTRY_TOKEN` di `~/.gradle/gradle.properties`
   (lihat `../README.md`) — `aipos-sdk` membawa `aipos-payment` di dalamnya, jadi
   kredensial MineSec wajib ada agar dependency bisa di-resolve, walau tap kartu
   sendiri tetap jalan lewat simulator tanpanya.
3. `./gradlew :app:assembleDebug`
