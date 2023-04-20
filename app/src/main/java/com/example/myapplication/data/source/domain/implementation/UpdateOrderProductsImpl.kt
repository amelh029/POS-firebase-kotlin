package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.UpdateOrderProducts
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderMixProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariantMix
import com.example.myapplication.data.source.local.room.OrdersDao
import com.example.myapplication.data.source.local.room.POSDao


class UpdateOrderProductsImpl(
    private val dao: OrdersDao,
    private val posDao: POSDao
) : UpdateOrderProducts {
    override suspend fun invoke(orderNo: String, products: List<ProductOrderDetail>) {
        dao.deleteDetailOrders(orderNo)
        insertOrderProduct(orderNo, products)
    }

    private suspend fun insertOrderProduct(orderNo: String, products: List<ProductOrderDetail>) {
        for (item in products) {
            if (item.product != null) {

                val detail = OrderDetail(orderNo, item.product!!.id, item.amount)
                detail.id = dao.insertDetailOrder(detail)

                if (item.product!!.isMix) {
                    for (p in item.mixProducts) {

                        // TODO: No stock needed for now
                        // productDao.decreaseProductStock(p.product.id, p.amount)

                        val variantMix = OrderProductVariantMix(detail.id, p.product.id, p.amount)
                        variantMix.id = posDao.insertVariantMixOrder(variantMix)

                        for (variant in p.variants) {
                            val mixVariant = OrderMixProductVariant(variantMix.id, variant.id)
                            mixVariant.id =
                                posDao.insertMixVariantOrder(mixVariant)
                        }
                    }
                } else {

                    // TODO: No stock needed for now
                    // productDao.decreaseProductStock(
                    //    item.product!!.id,
                    //    (item.amount * item.product!!.portion)
                    //)

                    for (variant in item.variants) {
                        val orderVariant = OrderProductVariant(detail.id, variant.id)
                        dao.insertVariantOrder(orderVariant)
                    }
                }
            }
        }
    }
}
