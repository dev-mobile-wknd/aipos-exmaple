package com.weekendinc.example.advisor

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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weekendinc.aipos.advisor.AdvisorClient
import com.weekendinc.aipos.advisor.AdvisorState
import com.weekendinc.aipos.advisor.ProductSuggestion
import com.weekendinc.aipos.domain.catalog.MutableProductCatalog

/**
 * Contoh penyaran produk dari percakapan sales–pelanggan (`aipos-advisor`).
 *
 * Dirakit lewat `AdvisorClient` — **bukan** `AIPosSDK` — supaya jelas bahwa host app
 * yang cuma butuh fitur AI ini tidak perlu menyeret modul pembayaran maupun MineSec
 * sama sekali. Tidak ada `.android(context)` di sini: penyaran murni Kotlin, tidak
 * menyentuh apa pun yang khusus platform.
 *
 * Di aplikasi sungguhan, [AdvisorClient.pushTranscript] dipanggil dari hasil
 * speech-to-text perangkat setiap kali satu kalimat pelanggan selesai dikenali. Contoh
 * ini memakai kotak teks sebagai pengganti mikrofon supaya alurnya bisa dicoba tanpa
 * izin `RECORD_AUDIO`.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val advisor = AdvisorClient.Builder()
            .llmApiKey(BuildConfig.LLM_API_KEY)
            .productCatalog(MutableProductCatalog(demoCatalog))
            .build()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdvisorScreen(advisor)
                }
            }
        }
    }
}

@Composable
private fun AdvisorScreen(advisor: AdvisorClient) {
    var transcriptInput by remember { mutableStateOf("") }
    val transcript by advisor.observeTranscript().collectAsStateWithLifecycle(initialValue = emptyList())
    val buyerIntent by advisor.observeBuyerIntent().collectAsStateWithLifecycle(initialValue = "")
    val suggestions by advisor.observeSuggestions().collectAsStateWithLifecycle(initialValue = emptyList())
    val state by advisor.observeAdvisorState().collectAsStateWithLifecycle(initialValue = AdvisorState.Idle)
    val error by advisor.observeAdvisorError().collectAsStateWithLifecycle(initialValue = null)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Ketik satu kalimat pelanggan lalu kirim — analisis berjalan sendiri", style = MaterialTheme.typography.bodySmall)

        OutlinedTextField(
            value = transcriptInput,
            onValueChange = { transcriptInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Kata pelanggan...") },
        )

        Button(onClick = {
            if (transcriptInput.isNotBlank()) {
                advisor.pushTranscript(transcriptInput)
                transcriptInput = ""
            }
        }) {
            Text("Kirim ke penyaran")
        }

        Text("Status: ${describeState(state)}", style = MaterialTheme.typography.labelMedium)
        error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

        if (buyerIntent.isNotBlank()) {
            Text("Kebutuhan pelanggan: $buyerIntent", style = MaterialTheme.typography.titleSmall)
        }

        Text("Saran (${suggestions.size})", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { suggestion -> SuggestionCard(suggestion) }
        }

        if (transcript.isNotEmpty()) {
            Text("Transkrip: ${transcript.joinToString(" / ")}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: ProductSuggestion) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${suggestion.product.name} — ${suggestion.product.price.format()}", style = MaterialTheme.typography.titleSmall)
            Text(suggestion.reason, style = MaterialTheme.typography.bodySmall)
            Text("Keyakinan: ${(suggestion.confidence * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun describeState(state: AdvisorState): String = when (state) {
    AdvisorState.Idle -> "Menunggu percakapan"
    AdvisorState.Thinking -> "Menganalisis..."
    AdvisorState.Suggesting -> "Ada saran"
}
