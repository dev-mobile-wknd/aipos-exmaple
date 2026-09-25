package com.weekendinc.example.payment

import android.app.Application
import android.util.Log
import com.weekendinc.aipos.AiPosAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application milik host app.
 *
 * `AiPosAndroid.initPlatform` wajib dipanggil dari `Application.onCreate` — MineSec
 * perlu memvalidasi lisensi dan menyiapkan kunci sebelum transaksi pertama. Tanpa
 * kredensial MineSec di Gradle (lihat `settings.gradle.kts`), ini otomatis jatuh ke
 * `SimulatorPaymentGateway`: tap kartu selalu disetujui, tanpa menyentuh perangkat keras.
 */
class PaymentExampleApp : Application() {

    private val appScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        appScope.launch {
            // Nama lisensinya bawaan SDK (`public-test.license`, ada di assets); host
            // app hanya perlu mengoper `licenseName` bila punya lisensi sendiri.
            val result = AiPosAndroid.initPlatform(application = this@PaymentExampleApp)
            Log.d(TAG, "Init terminal: $result")
        }
    }

    private companion object {
        const val TAG = "AiposExamplePayment"
    }
}
