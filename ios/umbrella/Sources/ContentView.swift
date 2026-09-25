import AIPosSDK
import SwiftUI

struct ContentView: View {
    @StateObject private var model = UmbrellaModel()
    @State private var input: String = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Keranjang: \(model.cart.items.count) item — \(model.cart.totalText)")
                .font(.subheadline.bold())
            Text("Pembayaran: \(model.paymentText)")
                .font(.caption)

            List(model.messages, id: \.timestamp) { message in
                Text(message.displayText).font(.body)
            }
            .listStyle(.plain)

            if model.isProcessing {
                Text("Agent sedang mengetik...").font(.caption).foregroundStyle(.secondary)
            }

            HStack {
                TextField("Contoh: \"Saya mau kopi susu satu, bayar kartu\"", text: $input)
                    .textFieldStyle(.roundedBorder)
                Button("Kirim") {
                    let text = input
                    guard !text.isEmpty else { return }
                    input = ""
                    model.send(text)
                }
            }
        }
        .padding(16)
    }
}
