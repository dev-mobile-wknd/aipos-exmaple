import AIPosSDK
import Foundation

/// Identitas merchant contoh.
///
/// `profileId` diisi eksplisit dengan bawaan SDK: nilai bawaan parameter Kotlin tidak
/// ikut terbawa ke Swift, jadi konstruktor di sini menuntut argumen ini walau di Kotlin
/// ia opsional.
let demoMerchant = MerchantInfo(
    id: "merchant-demo",
    name: "Toko Contoh AIPos",
    address: "Jl. Contoh No. 1, Jakarta",
    terminalId: "TID00001",
    mid: "MID00001",
    profileId: MerchantInfo.companion.DEFAULT_PROFILE_ID
)

/// Satu produk contoh — cukup untuk mendemokan alur bayar tanpa katalog sungguhan.
///
/// Dirakit lewat `productOf`, bukan konstruktor `Product` langsung: `Money` dan
/// `ProductId` adalah value class Kotlin yang tidak terbawa ke Swift.
let demoProduct: Product = IosDomainFactoryKt.productOf(
    id: "demo-kopi-susu",
    name: "Kopi Susu",
    priceRupiah: 25_000,
    barcode: "8990000000001",
    category: "Minuman",
    stock: 99,
    description: "",
    imageUrl: ""
)
