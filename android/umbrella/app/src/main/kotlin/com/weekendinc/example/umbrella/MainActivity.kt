package com.weekendinc.example.umbrella

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weekendinc.aipos.AIPosSDK
import com.weekendinc.aipos.AiPosAndroid
import com.weekendinc.aipos.agent.model.AgentMessage
import com.weekendinc.aipos.android
import com.weekendinc.aipos.domain.catalog.MutableProductCatalog
import com.weekendinc.aipos.domain.entity.Cart
import com.weekendinc.aipos.domain.model.PaymentState
import kotlinx.coroutines.launch

/**
 * Contoh umbrella `aipos-sdk`: satu `AIPosSDK` yang merakit AI agent kasir, penyaran
 * produk, keranjang, pembayaran terminal, dan pembayaran online sekaligus.
 *
 * Bedanya dengan contoh domain `payment` dan `advisor`: di sini kasir tidak perlu
 * menekan "tambah ke keranjang" atau "bayar" secara manual sama sekali — cukup ketik
 * permintaan pelanggan dalam Bahasa Indonesia, dan agent memanggil tool yang tepat
 * sendiri (`sendMessage`). Jalur langsung tanpa AI (`addToCart`, `processPayment`, dst.)
 * tetap tersedia berdampingan untuk kasus kasir menekan tombol produk secara manual —
 * lihat contoh domain `payment` untuk itu.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wajib sebelum Activity mencapai status STARTED.
        AiPosAndroid.attachActivity(this)

        val sdk = AIPosSDK.Builder()
            .llmApiKey(BuildConfig.LLM_API_KEY)
            .merchantInfo(demoMerchant)
            .productCatalog(MutableProductCatalog(demoCatalog))
            .android(this)
            .build()
        // `.onlinePayment(...)` sengaja tidak dipanggil: tanpa itu SDK memakai backend
        // beta bersama, cukup untuk demo tanpa backend merchant sendiri.

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChatScreen(sdk)
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(sdk: AIPosSDK) {
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    val messages by sdk.observeMessages().collectAsStateWithLifecycle(initialValue = emptyList())
    val isProcessing by sdk.observeProcessing().collectAsStateWithLifecycle(initialValue = false)
    val cart by sdk.observeCart().collectAsStateWithLifecycle(initialValue = Cart())
    val paymentState by sdk.observePaymentState().collectAsStateWithLifecycle(initialValue = PaymentState.Idle)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Keranjang: ${cart.items.size} item — ${cart.total.format()}", style = MaterialTheme.typography.titleSmall)
        Text("Pembayaran: ${describePayment(paymentState)}", style = MaterialTheme.typography.labelMedium)

        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages) { message -> MessageBubble(message) }
        }

        if (isProcessing) {
            Text("Agent sedang mengetik...", style = MaterialTheme.typography.labelSmall)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contoh: \"Saya mau kopi susu satu, bayar kartu\"") },
            )
            Button(onClick = {
                val text = input
                if (text.isNotBlank()) {
                    input = ""
                    scope.launch { sdk.sendMessage(text) }
                }
            }) {
                Text("Kirim")
            }
        }
    }
}

@Composable
private fun MessageBubble(message: AgentMessage) {
    val label = when (message) {
        is AgentMessage.UserMessage -> "Kasir: ${message.content}"
        is AgentMessage.AssistantMessage -> "Agent: ${message.content}"
        is AgentMessage.ToolCall -> "[tool] ${message.toolName} — ${message.summary}"
        is AgentMessage.ErrorMessage -> "[error] ${message.message}"
    }
    Text(label, style = MaterialTheme.typography.bodyMedium)
}

private fun describePayment(state: PaymentState): String = when (state) {
    is PaymentState.Idle -> "belum ada transaksi"
    is PaymentState.WaitingTap -> state.message
    is PaymentState.Processing -> state.message
    is PaymentState.Success -> "lunas (${state.transactionId})"
    is PaymentState.Failed -> "gagal — ${state.errorMessage}"
}
