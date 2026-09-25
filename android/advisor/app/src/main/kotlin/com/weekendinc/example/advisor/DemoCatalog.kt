package com.weekendinc.example.advisor

import com.weekendinc.aipos.domain.entity.Product
import com.weekendinc.aipos.domain.valueobject.Money
import com.weekendinc.aipos.domain.valueobject.ProductId

/**
 * Katalog contoh — beberapa produk lintas kategori, supaya penyaran punya sesuatu
 * yang bisa benar-benar dibedakan berdasarkan kebutuhan pelanggan di percakapan.
 */
val demoCatalog: List<Product> = listOf(
    Product(
        id = ProductId("laptop-ringan"),
        name = "Laptop Ultrabook 14\"",
        price = Money.fromRupiah(12_500_000),
        barcode = "8990000000101",
        category = "Laptop",
        stock = 5,
        description = "Ringan, baterai tahan seharian, cocok untuk kerja mobile.",
    ),
    Product(
        id = ProductId("laptop-gaming"),
        name = "Laptop Gaming 16\"",
        price = Money.fromRupiah(22_000_000),
        barcode = "8990000000102",
        category = "Laptop",
        stock = 3,
        description = "GPU kelas atas, layar refresh rate tinggi untuk gaming berat.",
    ),
    Product(
        id = ProductId("hp-kamera"),
        name = "Smartphone Kamera 108MP",
        price = Money.fromRupiah(6_500_000),
        barcode = "8990000000103",
        category = "Smartphone",
        stock = 10,
        description = "Fokus di kualitas kamera, cocok untuk konten kreator.",
    ),
    Product(
        id = ProductId("hp-hemat"),
        name = "Smartphone Baterai Jumbo",
        price = Money.fromRupiah(2_200_000),
        barcode = "8990000000104",
        category = "Smartphone",
        stock = 15,
        description = "Baterai 6000mAh, cocok untuk pemakaian seharian tanpa isi ulang.",
    ),
)
