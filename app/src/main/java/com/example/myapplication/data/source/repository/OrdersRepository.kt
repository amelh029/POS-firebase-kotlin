package com.example.myapplication.data.source.repository

import com.example.myapplication.data.source.local.entity.room.bridge.OrderPayment
import com.example.myapplication.data.source.local.entity.room.bridge.OrderPromo
import com.example.myapplication.data.source.local.entity.room.helper.OrderData
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.utils.tools.helper.ReportsParameter
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {

    fun getOrderList(status: Int, date: String): Flow<List<OrderData>>
    fun getOrderList(status: Int, date: String, store: Long): Flow<List<OrderData>>
    fun getOrderList(status: Int, parameters: ReportsParameter): Flow<List<OrderData>>
    fun getOrderData(orderNo: String): Flow<OrderData?>
    suspend fun getOrderDetail(orderNo: String): OrderData?
    suspend fun updateOrder(order: Order)
    suspend fun insertPaymentOrder(payment: OrderPayment): OrderPayment
    suspend fun insertNewPaymentOrder(payment: OrderPayment)
    suspend fun insertNewPromoOrder(promo: OrderPromo)
}
