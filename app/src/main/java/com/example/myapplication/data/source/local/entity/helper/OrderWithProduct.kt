package com.example.myapplication.data.source.local.entity.helper

import com.example.myapplication.data.source.local.entity.room.helper.OrderData
import java.io.Serializable

data class OrderWithProduct(
    var order: OrderData,
    var products: List<ProductOrderDetail>
): Serializable {
    constructor(order: OrderData): this(order, emptyList())

    val grandTotal: Long
        get() {
            return products.sumOf {
                it.product?.let { pr ->
                    pr.sellPrice * it.amount
                } ?: 0
            }
        }

    val totalPromo: Long
        get() = order.orderPromo?.totalPromo ?: 0L

    val grandTotalWithPromo: Long
        get() { return grandTotal - totalPromo}

    val totalItem: Int
        get() {
            return products.sumOf {
                it.amount
            }
        }
}