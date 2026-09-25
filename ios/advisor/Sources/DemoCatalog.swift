import AIPosSDK
import Foundation

/// Katalog contoh — beberapa produk lintas kategori, supaya penyaran punya sesuatu yang
/// bisa benar-benar dibedakan berdasarkan kebutuhan pelanggan di percakapan.
///
/// `Product` dirakit lewat `productOf`, bukan konstruktornya langsung: `Money` dan
/// `ProductId` adalah value class Kotlin yang tidak terbawa ke Swift.
let demoCatalog: [Product] = [
    IosDomainFactoryKt.productOf(
        id: "laptop-ringan", name: "Laptop Ultrabook 14\"", priceRupiah: 12_500_000,
        barcode: "8990000000101", category: "Laptop", stock: 5,
        description: "Ringan, baterai tahan seharian, cocok untuk kerja mobile.", imageUrl: ""
    ),
    IosDomainFactoryKt.productOf(
        id: "laptop-gaming", name: "Laptop Gaming 16\"", priceRupiah: 22_000_000,
        barcode: "8990000000102", category: "Laptop", stock: 3,
        description: "GPU kelas atas, layar refresh rate tinggi untuk gaming berat.", imageUrl: ""
    ),
    IosDomainFactoryKt.productOf(
        id: "hp-kamera", name: "Smartphone Kamera 108MP", priceRupiah: 6_500_000,
        barcode: "8990000000103", category: "Smartphone", stock: 10,
        description: "Fokus di kualitas kamera, cocok untuk konten kreator.", imageUrl: ""
    ),
    IosDomainFactoryKt.productOf(
        id: "hp-hemat", name: "Smartphone Baterai Jumbo", priceRupiah: 2_200_000,
        barcode: "8990000000104", category: "Smartphone", stock: 15,
        description: "Baterai 6000mAh, cocok untuk pemakaian seharian tanpa isi ulang.", imageUrl: ""
    ),
]
