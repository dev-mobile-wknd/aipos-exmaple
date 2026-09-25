# Contoh iOS — AI POS SDK

Padanan [`example/android`](../android), dengan pengelompokan domain yang sama. Beda
penting dari Android ada di paragraf berikutnya — baca dulu sebelum bingung kenapa
ketiga contoh menunjuk paket Swift yang sama persis.

| Contoh | Kelas SDK yang didemokan | Catatan |
|---|---|---|
| [`payment/`](payment) | `PaymentClient` (tap kartu), `OnlinePaymentClient` (VA/QRIS) | Tap kartu **selalu gagal** di iOS saat ini — lihat di bawah |
| [`advisor/`](advisor) | `AdvisorClient` (penyaran produk dari percakapan) | — |
| [`umbrella/`](umbrella) | `AIPosSDK` (semuanya: agent AI, penyaran, keranjang, pembayaran) | — |

## Kenapa ketiganya menunjuk paket Swift yang sama

Di Android, tiap modul (`aipos-payment`, `aipos-advisor`, dst.) dipublikasikan sebagai
artifact Maven terpisah, sehingga tiap contoh benar-benar hanya menarik satu modul.

Di iOS, **hanya ada satu distribusi**: `AIPosSDK.xcframework`, dipublikasikan lewat
Swift Package Manager di
[`dev-mobile-wknd/swift-aipos-sdk`](https://github.com/dev-mobile-wknd/swift-aipos-sdk).
XCFramework ini menggabungkan seluruh modul (`aipos-core`, `aipos-payment`,
`aipos-payment-online`, `aipos-advisor`, `aipos-agent`) jadi satu binary — tidak ada
`aipos-payment.xcframework` terpisah. Ini bukan kelalaian; itu keputusan arsitektur di
repo SDK (lihat `aipos-sdk/build.gradle.kts` dan `swift-aipos-sdk/README.md`).

Konsekuensinya: ketiga contoh di sini menambahkan dependency SPM yang **identik**
(`https://github.com/dev-mobile-wknd/swift-aipos-sdk`, produk `AIPosSDK`). Pemisahan
"per domain" di sini murni soal **kelas mana yang dipakai kodenya**
(`PaymentClient`/`OnlinePaymentClient` vs `AdvisorClient` vs `AIPosSDK`), bukan soal
dependency mana yang ditarik — beda dari Android, di mana pemisahannya juga
berarti dependency Gradle yang berbeda.

## Kenapa ada `project.yml`, bukan `.xcodeproj`

Ketiga contoh dibuat lewat [XcodeGen](https://github.com/yonaskolb/XcodeGen): setiap
folder punya `project.yml` (spesifikasi project, teks biasa) alih-alih `.xcodeproj`
biner yang di-commit. `Package.swift` murni **tidak bisa** dipakai di sini karena SPM
tidak bisa menghasilkan bundle `.app` iOS — hanya library dan command-line tool,
sehingga tetap butuh format project Xcode untuk sebuah aplikasi.

```bash
brew install xcodegen
cd example/ios/payment   # atau advisor / umbrella
xcodegen generate
open Payment.xcodeproj   # nama project mengikuti isi project.yml masing-masing
```

Build pertama akan mengunduh `AIPosSDK.xcframework` dari GitHub Release — tidak perlu
kredensial apa pun, repo distribusinya publik.

## Batasan yang perlu diketahui

- **Pembayaran kartu contactless belum didukung di iOS.** `PaymentClient.processPayment()`
  akan selalu mengembalikan `PaymentState.Failed` dengan pesan yang menjelaskan kenapa
  (`ProximityReader`/Tap to Pay memerlukan entitlement Apple yang belum ada di SDK ini).
  Contoh `payment/` sengaja tetap menampilkan jalur ini apa adanya — bukan disembunyikan
  — supaya perbedaannya terlihat.
- Beberapa tipe Kotlin (`Money`, `ProductId`) tidak terbawa utuh ke Swift sebagai value
  class; SDK menyediakan fungsi bantu (`productOf`, `formatMoneyCents`, dst.) untuk
  menutup celah itu. Lihat komentar di `Sources/SdkBridge.swift` tiap contoh.
- `Flow` Kotlin tidak punya padanan native di Swift — dijembatani lewat
  `FlowSubscriptionKt.subscribe(...)`, juga di `SdkBridge.swift`.
