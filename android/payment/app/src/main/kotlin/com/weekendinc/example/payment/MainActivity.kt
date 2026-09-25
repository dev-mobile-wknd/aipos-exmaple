package com.weekendinc.example.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weekendinc.aipos.AiPosAndroid
import com.weekendinc.aipos.domain.catalog.MutableProductCatalog
import com.weekendinc.aipos.domain.entity.WebPaymentSession
import com.weekendinc.aipos.domain.entity.WebPaymentStatus
import com.weekendinc.aipos.domain.model.PaymentState
import com.weekendinc.aipos.payment.PaymentClient
import com.weekendinc.aipos.payment.android
import com.weekendinc.aipos.payment.online.AiposPaymentWebViewClient
import com.weekendinc.aipos.payment.online.OnlinePaymentClient
import com.weekendinc.aipos.payment.online.android
import com.weekendinc.aipos.payment.online.paymentPageProbe
import com.weekendinc.aipos.payment.online.prepareForAiposPayment
import kotlinx.coroutines.launch

/**
 * Contoh dua cara menerima pembayaran: tap kartu di terminal (`aipos-payment`) dan
 * tautan Virtual Account / QRIS di WebView (`aipos-payment-online`).
 *
 * Keduanya dirakit berdiri sendiri lewat `PaymentClient`/`OnlinePaymentClient` —
 * **bukan** lewat `AIPosSDK` — supaya jelas bahwa host app yang hanya butuh pembayaran
 * tidak perlu menyeret AI agent maupun penyaran produk sama sekali.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wajib sebelum Activity mencapai status STARTED — lihat dokumentasi
        // AiPosAndroid.attachActivity.
        AiPosAndroid.attachActivity(this)

        val paymentClient = PaymentClient.Builder()
            .merchantInfo(demoMerchant)
            .productCatalog(MutableProductCatalog(listOf(demoProduct)))
            .android(this)
            .build()

        val onlineClient = OnlinePaymentClient.Builder()
            .merchantInfo(demoMerchant)
            .productCatalog(MutableProductCatalog(listOf(demoProduct)))
            .android(this)
            .build()
        // `config(...)` sengaja tidak dipanggil: tanpa itu SDK memakai backend beta
        // bersama milik AI POS SDK, cukup untuk demo tanpa backend merchant sendiri.

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PaymentExampleScreen(paymentClient, onlineClient)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Contoh sengaja tidak menutup client di sini — Activity ini adalah satu-satunya
        // layar aplikasi. Host app dengan banyak layar menutup client di titik yang
        // sepasang dengan tempat client itu dirakit.
    }
}

@Composable
private fun PaymentExampleScreen(
    paymentClient: PaymentClient,
    onlineClient: OnlinePaymentClient,
) {
    var tab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Tap Kartu") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Online (VA/QRIS)") })
        }

        when (tab) {
            0 -> ContactlessTab(paymentClient)
            else -> OnlineTab(onlineClient)
        }
    }
}

@Composable
private fun ContactlessTab(client: PaymentClient) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<PaymentState>(PaymentState.Idle) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("${demoProduct.name} — ${demoProduct.price.format()}", style = MaterialTheme.typography.titleMedium)
        Text(describeState(state))

        Button(onClick = {
            scope.launch {
                client.addToCart(demoProduct.id)
                client.processPayment().collect { state = it }
            }
        }) {
            Text("Tambahkan & bayar dengan kartu")
        }

        if (state.isTerminal) {
            Button(onClick = {
                scope.launch {
                    client.clearCart()
                    state = PaymentState.Idle
                }
            }) {
                Text("Transaksi baru")
            }
        }
    }
}

private fun describeState(state: PaymentState): String = when (state) {
    is PaymentState.Idle -> "Belum ada pembayaran berjalan."
    is PaymentState.WaitingTap -> state.message
    is PaymentState.Processing -> state.message
    is PaymentState.Success -> "Lunas — transaksi ${state.transactionId}, ${state.amount.format()}"
    is PaymentState.Failed -> "Gagal: ${state.errorMessage}"
}

@Composable
private fun OnlineTab(client: OnlinePaymentClient) {
    val scope = rememberCoroutineScope()
    var session by remember { mutableStateOf<WebPaymentSession?>(null) }
    val status by client.status.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("${demoProduct.name} — ${demoProduct.price.format()}", style = MaterialTheme.typography.titleMedium)
        Text(describeStatus(status))

        if (session == null) {
            Button(onClick = {
                scope.launch {
                    client.addToCart(demoProduct.id)
                    session = client.startSession().getOrNull()
                }
            }) {
                Text("Buat tautan pembayaran")
            }
        } else {
            PaymentWebView(client = client, url = session!!.paymentUrl)

            if (status.isFinal) {
                Button(onClick = {
                    scope.launch {
                        client.resetSession()
                        client.clearCart()
                        session = null
                    }
                }) {
                    Text("Transaksi baru")
                }
            }
        }
    }
}

private fun describeStatus(status: WebPaymentStatus): String = when (status) {
    is WebPaymentStatus.Idle -> "Belum ada pembayaran berjalan."
    is WebPaymentStatus.ChoosingMethod -> "Menunggu pelanggan memilih kanal pembayaran..."
    is WebPaymentStatus.Pending -> "Menunggu dana masuk (transaksi ${status.transactionId})..."
    is WebPaymentStatus.Success -> "Lunas — transaksi ${status.transactionId}"
    is WebPaymentStatus.Failed -> "Gagal: ${status.rawStatus ?: "tidak diketahui"}"
    is WebPaymentStatus.Cancelled -> "Dibatalkan."
    is WebPaymentStatus.Unknown -> "Status belum dikenal: ${status.rawStatus}"
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PaymentWebView(client: OnlinePaymentClient, url: String) {
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(480.dp),
        factory = { context ->
            WebView(context).apply {
                prepareForAiposPayment()
                webViewClient = AiposPaymentWebViewClient(client)
                client.attachPaymentPageProbe(paymentPageProbe())
                loadUrl(url)
            }
        },
    )
}
