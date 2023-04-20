package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.bridge.*
import com.example.myapplication.data.source.local.entity.room.helper.DetailProductMixWithVariantOption
import com.example.myapplication.data.source.local.entity.room.helper.DetailWithVariantMixOption
import com.example.myapplication.data.source.local.entity.room.helper.DetailWithVariantOption
import com.example.myapplication.data.source.local.entity.room.helper.OrderData
import com.example.myapplication.data.source.local.entity.room.master.Order
import kotlinx.coroutines.flow.Flow


@Dao
interface OrdersDao {
    @Transaction
    @Query("SELECT * FROM '${Order.DB_NAME}' WHERE ${Order.STATUS} = :status AND date(${Order.ORDER_DATE}) = date(:date)")
    fun getOrdersByStatus(status: Int, date: String): Flow<List<OrderData>>

    @Transaction
    @Query("SELECT * FROM '${Order.DB_NAME}' WHERE ${Order.STATUS} = :status AND date(${Order.ORDER_DATE}) = date(:date) AND ${Order.STORE} = :store")
    fun getOrdersByStatus(status: Int, date: String, store: Long): Flow<List<OrderData>>

    @Transaction
    @Query("SELECT * FROM '${Order.DB_NAME}' WHERE ${Order.STATUS} = :status AND ${Order.STORE} = :store AND date(${Order.ORDER_DATE}) BETWEEN date(:from) AND date(:until)")
    fun getOrdersByStatus(
        status: Int,
        from: String,
        until: String,
        store: Long
    ): Flow<List<OrderData>>

    @Transaction
    @Query("SELECT * FROM '${Order.DB_NAME}' WHERE ${Order.NO} = :orderNo")
    suspend fun getOrderByNo(orderNo: String): OrderData?

    @Transaction
    @Query("SELECT * FROM '${Order.DB_NAME}' WHERE ${Order.NO} = :orderNo")
    fun getOrderData(orderNo: String): Flow<OrderData?>

    @Query("SELECT * FROM ${OrderDetail.DB_NAME} WHERE ${Order.NO} = :orderNo")
    fun getDetailOrders(orderNo: String): Flow<List<OrderDetail>>

    @Transaction
    @Query("SELECT * FROM ${OrderDetail.DB_NAME} WHERE ${OrderDetail.ID} = :idDetail")
    suspend fun getOrderVariants(idDetail: Long): DetailWithVariantOption

    @Transaction
    @Query("SELECT * FROM ${OrderProductVariantMix.DB_NAME} WHERE ${OrderProductVariantMix.ID} = :idMix")
    suspend fun getOrderMixVariantsOption(idMix: Long): DetailProductMixWithVariantOption

    @Transaction
    @Query("SELECT * FROM ${OrderDetail.DB_NAME} WHERE ${OrderDetail.ID} = :idDetail")
    suspend fun getOrderVariantsMix(idDetail: Long): DetailWithVariantMixOption

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetailOrder(detail: OrderDetail): Long

    @Query("DELETE FROM ${OrderDetail.DB_NAME} WHERE ${Order.NO} = :orderNo")
    suspend fun deleteDetailOrders(orderNo: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVariantOrder(variants: OrderProductVariant): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPaymentOrder(payment: OrderPayment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPaymentOrder(payment: OrderPayment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPromoOrder(promo: OrderPromo)

    @Update
    suspend fun updateOrder(order: Order)
}
