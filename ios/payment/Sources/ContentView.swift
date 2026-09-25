import AIPosSDK
import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            ContactlessTab()
                .tabItem { Label("Tap Kartu", systemImage: "creditcard") }

            OnlineTab()
                .tabItem { Label("Online (VA/QRIS)", systemImage: "qrcode") }
        }
    }
}

private struct ContactlessTab: View {
    @StateObject private var model = ContactlessPaymentModel()

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("\(demoProduct.name) — \(demoProduct.priceText)")
                .font(.title3.bold())
            Text(model.statusText)

            Button("Tambahkan & bayar dengan kartu") { model.payNow() }
                .buttonStyle(.borderedProminent)

            if model.isTerminal {
                Button("Transaksi baru") { model.newTransaction() }
            }

            Spacer()
        }
        .padding(24)
    }
}

private struct OnlineTab: View {
    @StateObject private var model = OnlinePaymentModel()

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("\(demoProduct.name) — \(demoProduct.priceText)")
                .font(.title3.bold())
            Text(model.statusText)
            if let error = model.errorMessage {
                Text(error).foregroundStyle(.red)
            }

            if let session = model.session {
                Text(session.amountText).font(.title2.bold())

                PaymentWebView(
                    paymentUrl: session.paymentUrl,
                    onUrlChanged: { url in model.client.onWebViewUrlChanged(url: url) },
                    onLoadFailed: { reason in model.client.onWebViewLoadFailed(reason: reason) },
                    onWebViewReady: { probe in model.client.attachPaymentPageProbe(probe: probe) }
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity)

                if model.isFinal {
                    Button("Transaksi baru") { model.newTransaction() }
                }
            } else {
                Button("Buat tautan pembayaran") { model.createLink() }
                    .buttonStyle(.borderedProminent)
                Spacer()
            }
        }
        .padding(24)
    }
}
