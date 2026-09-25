import AIPosSDK
import Foundation

/// `profileId` diisi eksplisit dengan bawaan SDK: nilai bawaan parameter Kotlin tidak
/// ikut terbawa ke Swift.
let demoMerchant = MerchantInfo(
    id: "merchant-demo",
    name: "Toko Contoh AIPos",
    address: "Jl. Contoh No. 1, Jakarta",
    terminalId: "TID00001",
    mid: "MID00001",
    profileId: MerchantInfo.companion.DEFAULT_PROFILE_ID
)

/// Katalog contoh — cukup beragam supaya agent AI punya sesuatu untuk dibandingkan.
///
/// `Product` dirakit lewat `productOf`, bukan konstruktornya langsung: `Money` dan
/// `ProductId` adalah value class Kotlin yang tidak terbawa ke Swift.
let demoCatalog: [Product] = [
    IosDomainFactoryKt.productOf(
        id: "kopi-susu", name: "Kopi Susu", priceRupiah: 25_000,
        barcode: "8990000000001", category: "Minuman", stock: 50,
        description: "", imageUrl: ""
    ),
    IosDomainFactoryKt.productOf(
        id: "roti-bakar", name: "Roti Bakar Cokelat", priceRupiah: 18_000,
        barcode: "8990000000002", category: "Makanan", stock: 30,
        description: "", imageUrl: ""
    ),
    IosDomainFactoryKt.productOf(
        id: "es-teh", name: "Es Teh Manis", priceRupiah: 8_000,
        barcode: "8990000000003", category: "Minuman", stock: 100,
        description: "", imageUrl: ""
    ),
]
