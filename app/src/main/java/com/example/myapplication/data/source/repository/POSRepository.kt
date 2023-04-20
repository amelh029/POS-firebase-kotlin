package com.example.myapplication.data.source.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.data.NetworkBoundResource
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.room.bridge.OrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderMixProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariantMix
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.User
import com.example.myapplication.data.source.local.room.AppDatabase
import com.example.myapplication.data.source.local.room.POSDao
import com.example.myapplication.data.source.remote.response.entity.BatchWithData
import com.example.myapplication.data.source.remote.response.entity.BatchWithObject
import com.example.myapplication.utils.database.AppExecutors
import com.example.myapplication.utils.tools.helper.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class POSRepository private constructor(
    private val appExecutors: AppExecutors,
    private val POSDao: POSDao
) : POSDataSource {

    companion object {
        @Volatile
        private var INSTANCE: POSRepository? = null

        fun getInstance(
            appExecutors: AppExecutors,
            soliteDao: POSDao
        ): POSRepository {
            if (INSTANCE == null) {
                synchronized(POSRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = POSRepository(appExecutors, soliteDao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun replaceProductOrder(
        old: OrderWithProduct,
        new: OrderWithProduct
    ) {
        updateOrder(new.order.order)
        deleteOrderProduct(old)
        insertOrderProduct(new)
    }

    override fun updateOrder(order: Order) {
        POSDao.updateOrder(order)
    }

    private fun deleteOrderProduct(orderProduct: OrderWithProduct) {
        for (item in orderProduct.products) {
            if (item.product != null) {

                val detail = POSDao.getDetailOrders(
                    orderProduct.order.order.orderNo,
                    item.product!!.id
                )

                if (item.product!!.isMix) {
                    for (p in item.mixProducts) {

                        increaseStock(p.product.id, p.amount)

                        val variantMix = POSDao.getOrderProductVariantMix(
                            detail.id,
                            p.product.id
                        )

                        for (variant in p.variants) {
                            val variantMixOrder =
                                POSDao.getOrderMixProductVariant(
                                    variantMix.id,
                                    variant.id
                                )
                            POSDao.deleteOrderMixProductVariant(variantMixOrder)
                        }

                        POSDao.deleteOrderProductVariantMix(variantMix)
                    }
                } else {

                    increaseStock(item.product!!.id, (item.amount * item.product!!.portion))

                    for (variant in item.variants) {
                        val orderVariant =
                            POSDao.getVariantOrder(detail.id, variant.id)
                        POSDao.deleteOrderVariant(orderVariant)
                    }
                }

                POSDao.deleteOrderDetail(detail)
            }
        }
    }

    private fun insertOrderProduct(order: OrderWithProduct) {
        for (item in order.products) {
            if (item.product != null) {

                val detail = OrderDetail(order.order.order.orderNo, item.product!!.id, item.amount)
                detail.id = POSDao.insertDetailOrder(detail)

                if (item.product!!.isMix) {
                    for (p in item.mixProducts) {

                        POSDao.decreaseAndGetProduct(p.product.id, p.amount)

                        val variantMix = OrderProductVariantMix(detail.id, p.product.id, p.amount)
                        variantMix.id = POSDao.insertVariantMixOrder(variantMix)

                        for (variant in p.variants) {
                            val mixVariant = OrderMixProductVariant(variantMix.id, variant.id)
                            mixVariant.id =
                                POSDao.insertMixVariantOrder(mixVariant)
                        }
                    }
                } else {

                    POSDao.decreaseAndGetProduct(
                        item.product!!.id,
                        (item.amount * item.product!!.portion)
                    )

                    for (variant in item.variants) {
                        val orderVariant = OrderProductVariant(detail.id, variant.id)
                        POSDao.insertVariantOrder(orderVariant)
                    }
                }
            }
        }
    }

    private fun increaseStock(idProduct: Long, amount: Int)
            : BatchWithObject<Product> {
        val product = POSDao.increaseAndGetProduct(idProduct, amount)
        val doc = Firebase.firestore
            .collection(AppDatabase.DB_NAME)
            .document(AppDatabase.MAIN)
            .collection(Product.DB_NAME)
            .document(product.id.toString())
        return BatchWithObject(product, BatchWithData(doc, Product.toHashMap(product)))
    }

    override fun getUsers(userId: String): LiveData<Resource<User?>> {
        return object : NetworkBoundResource<User?, List<User>>(appExecutors) {
            override fun loadFromDB(): LiveData<User?> {
                return POSDao.getUser(userId)
            }

        }.asLiveData()
    }
}