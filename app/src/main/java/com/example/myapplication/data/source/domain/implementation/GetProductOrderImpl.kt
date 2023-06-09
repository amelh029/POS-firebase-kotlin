package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.GetProductOrder
import com.example.myapplication.data.source.local.entity.helper.ProductMixOrderDetail
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderDetail
import com.example.myapplication.data.source.local.room.OrdersDao
import com.example.myapplication.data.source.local.room.ProductsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetProductOrderImpl(
    private val dao: OrdersDao,
    private val productsDao: ProductsDao,
) : GetProductOrder {
    override fun invoke(orderNo: String): Flow<List<ProductOrderDetail>> {
        return dao.getDetailOrders(orderNo)
            .map {
                handleListDetail(it)
            }
    }

    private suspend fun handleListDetail(listDetail: List<OrderDetail>): List<ProductOrderDetail> {
        val products = mutableListOf<ProductOrderDetail>()
        for (item2 in listDetail) {
            val product = productsDao.getProduct(item2.idProduct)
            if (product.isMix) {
                val mixes = dao.getOrderVariantsMix(item2.id)
                val mixProduct = mutableListOf<ProductMixOrderDetail>()
                for (mix in mixes.variantsMix) {
                    val variants = dao.getOrderMixVariantsOption(mix.id)
                    mixProduct.add(
                        ProductMixOrderDetail(
                            productsDao.getProduct(mix.idProduct),
                            variants.options,
                            mix.amount
                        )
                    )
                }
                products.add(ProductOrderDetail.createMix(product, mixProduct, item2.amount))
            } else {
                val variants = dao.getOrderVariants(item2.id)
                products.add(
                    ProductOrderDetail.createProduct(
                        product,
                        variants.options,
                        item2.amount
                    )
                )
            }
        }
        return products
    }
}
