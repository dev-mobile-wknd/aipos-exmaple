import AIPosSDK
import SwiftUI

struct ContentView: View {
    @StateObject private var model = AdvisorModel()
    @State private var input: String = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Ketik satu kalimat pelanggan lalu kirim — analisis berjalan sendiri")
                .font(.caption)
                .foregroundStyle(.secondary)

            HStack {
                TextField("Kata pelanggan...", text: $input)
                    .textFieldStyle(.roundedBorder)
                Button("Kirim") {
                    let text = input
                    guard !text.isEmpty else { return }
                    input = ""
                    model.send(text)
                }
            }

            Text("Status: \(model.stateText)").font(.caption.bold())
            if let error = model.error {
                Text("Error: \(error)").foregroundStyle(.red)
            }

            if !model.buyerIntent.isEmpty {
                Text("Kebutuhan pelanggan: \(model.buyerIntent)").font(.headline)
            }

            Text("Saran (\(model.suggestions.count))").font(.title3.bold())
            List(model.suggestions, id: \.reason) { suggestion in
                VStack(alignment: .leading, spacing: 4) {
                    Text("\(suggestion.product.name) — \(suggestion.product.priceText)")
                        .font(.subheadline.bold())
                    Text(suggestion.reason).font(.caption)
                    Text("Keyakinan: \(Int(suggestion.confidence * 100))%")
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }
            }
            .listStyle(.plain)
        }
        .padding(24)
    }
}
