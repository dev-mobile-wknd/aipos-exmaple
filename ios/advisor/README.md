# Contoh: Penyaran Produk AI (`AdvisorClient`)

Padanan iOS dari [`example/android/advisor`](../../android/advisor). Kotak teks
mensimulasikan transkrip percakapan sales–pelanggan. Setiap kalimat yang dikirim lewat
`AdvisorClient.pushTranscript(text:)` memicu analisis (setelah percakapan "mereda"
sejenak), dan hasilnya muncul lewat `observeSuggestions()` dan `observeBuyerIntent()`.
Lihat [`AdvisorModel.swift`](Sources/AdvisorModel.swift).

Di aplikasi sungguhan, `pushTranscript` dipanggil dari hasil speech-to-text perangkat —
bukan kotak teks — setiap kali satu kalimat pelanggan selesai dikenali.

`AdvisorClient` dirakit tanpa `.ios()`: penyaran murni Kotlin, tidak menyentuh apa pun
yang khusus platform.

## Menjalankan

```bash
brew install xcodegen   # sekali saja
xcodegen generate
open Advisor.xcodeproj
```

Isi kunci OpenAI lewat environment variable **sebelum** membuka Xcode, atau lewat
**Product ▸ Scheme ▸ Edit Scheme… ▸ Run ▸ Arguments ▸ Environment Variables** di Xcode:

```bash
LLM_API_KEY=sk-... open Advisor.xcodeproj
```

Tanpa `LLM_API_KEY`, `AdvisorClient.Builder().build()` melempar exception saat
dirakit — bukan gagal diam-diam saat dipakai.
