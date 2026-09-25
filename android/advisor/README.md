# Contoh: Penyaran Produk AI (`aipos-advisor`)

Kotak teks mensimulasikan transkrip percakapan sales–pelanggan. Setiap kalimat yang
dikirim lewat `AdvisorClient.pushTranscript(...)` memicu analisis (setelah percakapan
"mereda" sejenak), dan hasilnya muncul lewat `observeSuggestions()` dan
`observeBuyerIntent()`. Lihat [`MainActivity.kt`](app/src/main/kotlin/com/weekendinc/example/advisor/MainActivity.kt).

Di aplikasi sungguhan, `pushTranscript` dipanggil dari hasil speech-to-text
perangkat — bukan kotak teks — setiap kali satu kalimat pelanggan selesai dikenali.

`AdvisorClient` dirakit tanpa `.android(context)`: penyaran murni Kotlin, tidak
menyentuh MineSec, NFC, atau apa pun yang khusus platform.

## Menjalankan

1. Salin `app/local.properties.example` → `app/local.properties`, isi `LLM_API_KEY`
   dengan kunci OpenAI Anda.
2. Isi `GITHUB_PACKAGES_LOGIN`/`GITHUB_PACKAGES_TOKEN` di `~/.gradle/gradle.properties`
   (lihat `../README.md`) — modul ini tidak butuh kredensial MineSec sama sekali.
3. `./gradlew :app:assembleDebug`

Tanpa `LLM_API_KEY`, `AdvisorClient.Builder().build()` melempar
`IllegalArgumentException` saat dirakit — bukan gagal diam-diam saat dipakai.
