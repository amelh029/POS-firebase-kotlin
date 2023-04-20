package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.GetOrdersGeneralMenuBadge
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.repository.OrdersRepository
import kotlinx.coroutines.flow.zip

class GetOrdersGeneralMenuBadgeImpl(
    private val ordersRepository: OrdersRepository
) : GetOrdersGeneralMenuBadge {
    override fun invoke(date: String) = ordersRepository.getOrderList(Order.ON_PROCESS, date)
        .zip(ordersRepository.getOrderList(Order.NEED_PAY, date)) { process, needPay ->
            process.size + needPay.size
        }
}
