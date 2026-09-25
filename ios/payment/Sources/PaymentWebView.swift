import AIPosSDK
import SwiftUI
import WebKit

/// WebView yang memuat halaman pembayaran.
///
/// SDK menyediakan `AiposPaymentNavigationDelegate` untuk peran ini, tapi kelas itu
/// **tidak bisa dipakai dari Swift** — ia turunan `NSObject` di sisi Kotlin, dan header
/// framework menandainya `unavailable`. Delegate-nya karena itu ditulis ulang di sini;
/// yang dipanggil tetap fungsi SDK yang sama lewat `onUrlChanged`/`onLoadFailed`, yang di
/// pemanggil (`ContentView`) diteruskan ke `client.onWebViewUrlChanged`/`onWebViewLoadFailed`.
struct PaymentWebView: UIViewRepresentable {

    let paymentUrl: String
    let onUrlChanged: (String) -> Void
    let onLoadFailed: (String) -> Void
    /// Menyerahkan WebView ke SDK begitu dibuat. **Tanpa ini status pembayaran tidak
    /// akan pernah bergerak** — halaman pembayaran berpindah layar tanpa mengubah
    /// alamatnya, jadi menyimak URL saja tidak akan melihat apa pun.
    let onWebViewReady: (any PaymentPageProbe) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(onUrlChanged: onUrlChanged, onLoadFailed: onLoadFailed)
    }

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView(frame: .zero)
        webView.navigationDelegate = context.coordinator
        context.coordinator.observeUrl(of: webView)

        onWebViewReady(IosPaymentPageProbeKt.paymentPageProbe(webView))

        if let url = URL(string: paymentUrl) {
            webView.load(URLRequest(url: url))
        } else {
            onLoadFailed("Alamat halaman pembayaran tidak valid: \(paymentUrl)")
        }

        return webView
    }

    /// Sengaja tidak memuat ulang apa pun. Status berubah beberapa kali selama
    /// pembayaran, dan setiap pemuatan ulang akan melempar pelanggan kembali ke awal.
    func updateUIView(_ uiView: WKWebView, context: Context) {}

    final class Coordinator: NSObject, WKNavigationDelegate {

        private let onUrlChanged: (String) -> Void
        private let onLoadFailed: (String) -> Void
        private var urlObservation: NSKeyValueObservation?

        init(onUrlChanged: @escaping (String) -> Void, onLoadFailed: @escaping (String) -> Void) {
            self.onUrlChanged = onUrlChanged
            self.onLoadFailed = onLoadFailed
        }

        deinit {
            urlObservation?.invalidate()
        }

        /// `WKWebView` tidak memanggil delegate saat halaman berpindah lewat
        /// `history.pushState` — dan itulah cara halaman pembayaran (aplikasi satu
        /// halaman) berpindah keadaan. KVO atas `url` menangkap perpindahan itu.
        func observeUrl(of webView: WKWebView) {
            urlObservation = webView.observe(\.url, options: [.new]) { [weak self] _, change in
                guard let url = change.newValue??.absoluteString else { return }
                self?.onUrlChanged(url)
            }
        }

        func webView(
            _ webView: WKWebView,
            decidePolicyFor navigationAction: WKNavigationAction,
            decisionHandler: @escaping (WKNavigationActionPolicy) -> Void
        ) {
            if let url = navigationAction.request.url?.absoluteString {
                onUrlChanged(url)
            }
            decisionHandler(.allow)
        }

        func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
            onLoadFailed(error.localizedDescription)
        }

        func webView(
            _ webView: WKWebView,
            didFailProvisionalNavigation navigation: WKNavigation!,
            withError error: Error
        ) {
            onLoadFailed(error.localizedDescription)
        }
    }
}
