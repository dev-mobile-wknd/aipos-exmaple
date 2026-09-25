package com.weekendinc.example.umbrella

import android.app.Application
import android.util.Log
import com.weekendinc.aipos.AiPosAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application milik host app.
 *
 * Sama seperti contoh domain pembayaran: `AiPosAndroid.initPlatform` wajib dipanggil
 * dari `onCreate` karena `aipos-sdk` membawa `aipos-payment` di dalamnya. Tanpa
 * kredensial MineSec di Gradle, ini otomatis jatuh ke `SimulatorPaymentGateway`.
 */
class UmbrellaExampleApp : Application() {

    private val appScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        appScope.launch {
            val result = AiPosAndroid.initPlatform(application = this@UmbrellaExampleApp)
            Log.d(TAG, "Init terminal: $result")
        }
    }

    private companion object {
        const val TAG = "AiposExampleUmbrella"
    }
}
