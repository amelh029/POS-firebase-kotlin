package com.example.myapplication.data.source.local.entity.helper

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.PurchaseProduct
import java.io.Serializable


data class PurchaseProductWithProduct(
    @Embedded
    var purchaseProduct: PurchaseProduct?,

    @Relation(parentColumn = Product.ID, entityColumn = Product.ID)
    var product: Product?,

    @Ignore
    var type: Int?
): Serializable {

    constructor(purchaseProduct: PurchaseProduct?, product: Product?): this(purchaseProduct, product, null)

    @Ignore
    constructor(type: Int?): this(null, null, type)
    companion object{

        private const val TITLE = 1
        private const val GRAND = 2

        val title: PurchaseProductWithProduct
            get() {
                return PurchaseProductWithProduct(TITLE)
            }

        val grand: PurchaseProductWithProduct
            get() {
                return PurchaseProductWithProduct(GRAND)
            }
    }
}
