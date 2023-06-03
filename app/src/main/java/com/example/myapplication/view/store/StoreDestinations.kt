package com.example.myapplication.view.store

object StoreDestinations {

    const val RESERVES_ID = "reserves_id"
    const val PRODUCT_ID = "product_id"

    const val MAIN_STORE = "main_store"
    const val MASTER_STORES = "master_stores"
    const val MASTER_RECAP = "master_recap"
    const val MASTER_PRODUCT = "master_product"
    const val MASTER_VARIANTS = "master_variants"
    const val MASTER_CATEGORY = "master_category"
    const val MASTER_PAYMENT = "master_payment"
    const val MASTER_PROMO = "master_promo"
    const val MASTER_RESERVES = "master_reserves"
    const val MASTER_RESERVES_CATEGORY = "master_reserves_category"

    const val DETAIL_PRODUCT = "detail_product/{$PRODUCT_ID}"
    const val PRODUCT_VARIANTS = "product_variants/{$PRODUCT_ID}"
    const val DETAIL_RESERVES = "detail_reserves/{$RESERVES_ID}"
    fun productDetail(productId: Long) = "detail_product/$productId"
    fun reservesDetail(reservesId: Long) = "detail_reserves/$reservesId"
    fun newProduct() = "detail_product/0"
    fun newReserves() = "detail_reserves/0"
    fun productVariants(productId: Long) = "product_variants/$productId"
}
