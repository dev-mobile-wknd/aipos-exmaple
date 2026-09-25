# Contoh: Umbrella (`AIPosSDK`)

Padanan iOS dari [`example/android/umbrella`](../../android/umbrella). Satu titik masuk
`AIPosSDK` yang merakit seluruh fitur sekaligus: agent kasir AI, penyaran produk,
keranjang, pembayaran terminal, dan pembayaran online.

Layarnya satu kotak chat. Ketik permintaan pelanggan dalam Bahasa Indonesia — misal
*"saya mau kopi susu satu, bayar kartu"* — dan `sdk.sendMessage(message:)`
menyerahkannya ke AI agent, yang memanggil tool pencarian produk, keranjang, dan
pembayaran sendiri. Status keranjang dan pembayaran ikut ditampilkan lewat
`observeCart()` dan `observePaymentState()`. Lihat
[`UmbrellaModel.swift`](Sources/UmbrellaModel.swift).

**Pembayaran kartu akan selalu gagal** bila agent mencoba memprosesnya — Tap to Pay
belum didukung di iOS (lihat [`../payment/README.md`](../payment/README.md)). Jalur
langsung tanpa AI (`addToCart`, `processPayment`, dst.) tetap ada di `AIPosSDK` untuk
kasus kasir menekan tombol produk secara manual — didemokan terpisah di contoh
[`payment/`](../payment) supaya tidak bercampur dengan alur AI di sini.

## Menjalankan

```bash
brew install xcodegen   # sekali saja
xcodegen generate
open Umbrella.xcodeproj
```

Isi kunci OpenAI lewat environment variable **sebelum** membuka Xcode, atau lewat
**Product ▸ Scheme ▸ Edit Scheme… ▸ Run ▸ Arguments ▸ Environment Variables** di Xcode:

```bash
LLM_API_KEY=sk-... open Umbrella.xcodeproj
```
