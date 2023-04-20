package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.GetProductOrder
import com.example.myapplication.data.source.domain.GetRecapData
import com.example.myapplication.data.source.local.entity.helper.Income
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.helper.RecapData
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.repository.OrdersRepository
import com.example.myapplication.data.source.repository.OutcomesRepository
import com.example.myapplication.utils.tools.helper.ReportsParameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetRecapDataImpl(
    private val repository: OrdersRepository,
    private val outcomesRepository: OutcomesRepository,
    private val getProductOrder: GetProductOrder
) : GetRecapData {
    override fun invoke(parameters: ReportsParameter): Flow<RecapData> {

        return flow {
            val incomes = repository.getOrderList(Order.DONE, parameters).first().map {
                val products = getProductOrder.invoke(it.order.orderNo).first()
                val orderWithProduct = OrderWithProduct(it, products)

                Income(
                    date = it.order.orderTime,
                    total = orderWithProduct.grandTotalWithPromo,
                    payment = it.payment?.name ?: "",
                    isCash = it.payment?.isCash ?: false
                )
            }
            val outcomes = outcomesRepository.getOutcomes(parameters).first()

            emit(
                RecapData(
                    incomes = incomes,
                    outcomes = outcomes
                )
            )
        }

    }
}
