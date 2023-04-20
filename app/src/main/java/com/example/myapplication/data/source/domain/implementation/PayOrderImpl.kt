package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.PayOrder
import com.example.myapplication.data.source.local.entity.room.bridge.OrderPayment
import com.example.myapplication.data.source.local.entity.room.bridge.OrderPromo
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.repository.OrdersRepository


class PayOrderImpl(
    private val ordersRepository: OrdersRepository
) : PayOrder {
    override suspend fun invoke(order: Order, payment: OrderPayment, promo: OrderPromo?) {
        promo?.let {
            ordersRepository.insertNewPromoOrder(promo)
        }
        ordersRepository.insertNewPaymentOrder(payment)
        ordersRepository.updateOrder(order.copy(
            status = Order.DONE
        ))
    }
}
