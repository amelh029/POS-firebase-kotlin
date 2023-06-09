package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.NewOrder
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderMixProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariantMix
import com.example.myapplication.data.source.local.entity.room.helper.OrderData
import com.example.myapplication.data.source.local.entity.room.master.Customer
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.room.OrdersDao
import com.example.myapplication.data.source.local.room.POSDao
import com.example.myapplication.data.source.preference.OrderPref
import com.example.myapplication.data.source.repository.SettingRepository
import com.example.myapplication.utils.config.DateUtils
import kotlinx.coroutines.flow.first

class NewOrderImpl(
    private val dao: OrdersDao,
//    private val productDao: ProductsDao,
    private val posDao: POSDao,
    private val orderPref: OrderPref,
    private val settingRepository: SettingRepository
) : NewOrder {
    override suspend fun invoke(
        customer: Customer,
        isTakeAway: Boolean,
        products: List<ProductOrderDetail>,
        currentTime: String
    ) {
        val selectedStore = settingRepository.getSelectedStore().first()
        val orderData = generateOrder(customer, isTakeAway, currentTime, selectedStore)
        val orderWithProducts = OrderWithProduct(
            order = orderData,
            products = products
        )
        dao.insertOrder(orderWithProducts.order.order)
        insertOrderProduct(orderWithProducts)
        increaseOrderQueue()
    }

    private fun generateOrder(
        customer: Customer,
        isTakeAway: Boolean,
        currentTime: String,
        store: Long
    ): OrderData {
        val order = Order(
            orderNo = generateOrderNo(currentTime),
            customer = customer.id,
            orderTime = currentTime,
            store = store,
            isTakeAway = isTakeAway,
        )
        return OrderData.newOrder(
            order = order,
            customer = customer
        )
    }

    private fun generateOrderNo(
        currentTime: String
    ): String {
        val time = generateOrderNoFromDate(currentTime)
        if (time != orderPref.orderDate) {
            saveDate(time)
        }
        val count = orderPref.orderCount
        return "${orderPref.orderDate}${generateQueueNumber(count)}"
    }

    private fun saveDate(time: String) {
        orderPref.orderDate = time
        orderPref.orderCount = 1
    }

    private fun generateOrderNoFromDate(time: String) =
        DateUtils.convertDateFromDate(time, DateUtils.DATE_ORDER_NO_FORMAT)

    private fun generateQueueNumber(i: Int): String {
        var str = i.toString()
        when (str.length) {
            1 -> str = "00$i"
            2 -> str = "0$i"
        }
        return str
    }

    private fun increaseOrderQueue() {
        orderPref.orderCount = orderPref.orderCount + 1
    }

    private suspend fun insertOrderProduct(order: OrderWithProduct) {
        for (item in order.products) {
            if (item.product != null) {

                val detail = OrderDetail(order.order.order.orderNo, item.product.id, item.amount)
                detail.id = dao.insertDetailOrder(detail)

                if (item.product.isMix) {
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
