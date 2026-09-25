# Contoh Android — AI POS SDK

Tiga aplikasi contoh, dikelompokkan per domain, masing-masing **project Gradle
berdiri sendiri** (punya `gradlew` sendiri) yang menarik AI POS SDK dari GitHub
Packages — bukan dari source. Folder ini dirancang untuk suatu saat dipindah jadi
repository publik tersendiri tanpa perlu diubah.

| Contoh | Modul SDK yang didemokan | Butuh MineSec? | Butuh kunci LLM? |
|---|---|---|---|
| [`payment/`](payment) | `aipos-payment` (tap kartu), `aipos-payment-online` (VA/QRIS) | Ya (untuk resolve, tap tetap jalan tanpanya lewat simulator) | Tidak |
| [`advisor/`](advisor) | `aipos-advisor` (penyaran produk dari percakapan) | Tidak | Ya |
| [`umbrella/`](umbrella) | `aipos-sdk` (semuanya: agent AI, penyaran, keranjang, pembayaran terminal & online) | Ya | Ya |

Tiap contoh sengaja minimal: satu Activity, satu layar (atau beberapa tab), fokus
memperlihatkan cara memakai API publik modul itu — bukan aplikasi kasir yang lengkap.
Untuk aplikasi contoh yang lebih lengkap (grid produk, keranjang, checkout, speech-to-text),
lihat `sample-android` di repository utama SDK.

## Kredensial

Semua contoh butuh akses ke registry privat AI POS SDK. Simpan di
`~/.gradle/gradle.properties` (JANGAN di dalam proyek):

```properties
# Wajib untuk KETIGA contoh — personal access token GitHub dengan scope `read:packages`.
GITHUB_PACKAGES_LOGIN=<username GitHub yang diberi akses>
GITHUB_PACKAGES_TOKEN=<token>

# Wajib untuk `payment/` dan `umbrella/` saja — diminta ke MineSec customer support.
# Tanpa ini build APK tetap gagal (401 saat resolve dependency), BUKAN cuma tap yang
# gagal — aipos-payment menarik com.theminesec.sdk:headless-stage saat runtime.
MINESEC_REGISTRY_LOGIN=<login>
MINESEC_REGISTRY_TOKEN=<token>
```

Kunci LLM (untuk `advisor/` dan `umbrella/`) berbeda jalur — masing-masing modulnya
punya `app/local.properties.example` sendiri, disalin jadi `local.properties` di
folder yang sama.

## Menjalankan

```bash
cd example/android/payment   # atau advisor / umbrella
./gradlew :app:assembleDebug
```

Tiap folder independen — `cd` ke salah satunya dan jalankan `./gradlew` dari sana,
bukan dari `example/android/` atau root repository.
