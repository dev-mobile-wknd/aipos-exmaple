package com.weekendinc.example.umbrella

import com.weekendinc.aipos.domain.entity.MerchantInfo
import com.weekendinc.aipos.domain.entity.Product
import com.weekendinc.aipos.domain.valueobject.Money
import com.weekendinc.aipos.domain.valueobject.ProductId

/**
 * `profileId` dikosongkan supaya cocok dengan [MerchantInfo.DEFAULT_PROFILE_ID] — profil
 * lingkungan uji MineSec yang sepasang dengan lisensi `public-test.license` di `assets`.
 */
val demoMerchant = MerchantInfo(
    id = "merchant-demo",
    name = "Toko Contoh AIPos",
    address = "Jl. Contoh No. 1, Jakarta",
    terminalId = "TID00001",
    mid = "MID00001",
)

/** Katalog contoh — cukup beragam supaya agent AI punya sesuatu untuk dibandingkan. */
val demoCatalog: List<Product> = listOf(
    Product(
        id = ProductId("kopi-susu"),
        name = "Kopi Susu",
        price = Money.fromRupiah(25_000),
        barcode = "8990000000001",
        category = "Minuman",
        stock = 50,
    ),
    Product(
        id = ProductId("roti-bakar"),
        name = "Roti Bakar Cokelat",
        price = Money.fromRupiah(18_000),
        barcode = "8990000000002",
        category = "Makanan",
        stock = 30,
    ),
    Product(
        id = ProductId("es-teh"),
        name = "Es Teh Manis",
        price = Money.fromRupiah(8_000),
        barcode = "8990000000003",
        category = "Minuman",
        stock = 100,
    ),
)
