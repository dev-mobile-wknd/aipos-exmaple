package com.weekendinc.example.payment

import com.weekendinc.aipos.domain.entity.MerchantInfo
import com.weekendinc.aipos.domain.entity.Product
import com.weekendinc.aipos.domain.valueobject.Money
import com.weekendinc.aipos.domain.valueobject.ProductId

/**
 * Identitas merchant contoh.
 *
 * `profileId` dikosongkan supaya cocok dengan [MerchantInfo.DEFAULT_PROFILE_ID] — profil
 * lingkungan uji MineSec yang sepasang dengan lisensi `public-test.license` di `assets`.
 * Merchant sungguhan mengoper profil dan TID/MID miliknya sendiri.
 */
val demoMerchant = MerchantInfo(
    id = "merchant-demo",
    name = "Toko Contoh AIPos",
    address = "Jl. Contoh No. 1, Jakarta",
    terminalId = "TID00001",
    mid = "MID00001",
)

/** Satu produk contoh — cukup untuk mendemokan alur bayar tanpa katalog sungguhan. */
val demoProduct = Product(
    id = ProductId("demo-kopi-susu"),
    name = "Kopi Susu",
    price = Money.fromRupiah(25_000),
    barcode = "8990000000001",
    category = "Minuman",
    stock = 99,
)
