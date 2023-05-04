package com.example.myapplication.view.viewModel


import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.source.domain.*
import com.example.myapplication.data.source.local.entity.helper.BucketOrder
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderPayment
import com.example.myapplication.data.source.local.entity.room.bridge.OrderPromo
import com.example.myapplication.data.source.local.entity.room.master.Customer
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.entity.room.master.Payment
import com.example.myapplication.data.source.local.entity.room.master.Promo
import com.example.myapplication.data.source.repository.OrdersRepository
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.utils.tools.helper.ReportsParameter
import com.example.myapplication.view.ui.OrderMenus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class OrderViewModel (
    private val orderRepository: OrdersRepository,
    private val newOrder: NewOrder,
    private val getProductOrder: GetProductOrder,
    private val getRecapData: GetRecapData,
    private val payOrder: PayOrder,
    private val getOrdersGeneralMenuBadge: GetOrdersGeneralMenuBadge,
    private val updateOrderProducts: UpdateOrderProducts,
) : ViewModel() {

    companion object : ViewModelFromFactory<OrderViewModel>() {
        fun getOrderViewModel(activity: FragmentActivity): OrderViewModel {
            return buildViewModel(activity, OrderViewModel::class.java)
        }
    }

    private val _currentBucket = MutableStateFlow(BucketOrder.idle())
    val currentBucket = _currentBucket.asStateFlow()

    fun getOrderList(status: Int, parameters: ReportsParameter) =
        orderRepository.getOrderList(status, parameters)

    fun getOrderBadge(orderMenus: OrderMenus, parameters: ReportsParameter) = when (orderMenus) {
        OrderMenus.CURRENT_ORDER, OrderMenus.NOT_PAY_YET -> {
            orderRepository
                .getOrderList(orderMenus.status, parameters)
                .map {
                    if (it.isEmpty()) null else it.size
                }
        }

        else -> {
            flowOf(null)
        }
    }

    fun getMenuBadge(date: String) = getOrdersGeneralMenuBadge(date)

    suspend fun getOrderDetail(orderNo: String) = orderRepository.getOrderDetail(orderNo)
    fun getOrderData(orderNo: String) = orderRepository.getOrderData(orderNo)

    fun getProductOrder(orderNo: String) = getProductOrder.invoke(orderNo)

    fun getIncomes(parameters: ReportsParameter) = getRecapData(parameters)

    fun newOrder(
        customer: Customer,
        isTakeAway: Boolean,
    ) {
        viewModelScope.launch {
            _currentBucket.value.products?.let {
                newOrder.invoke(
                    customer = customer,
                    isTakeAway = isTakeAway,
                    products = it,
                    currentTime = DateUtils.currentDateTime
                )
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.updateOrder(order)
        }
    }

    fun updateOrderProducts(orderNo: String) {
        viewModelScope.launch {
            _currentBucket.value.products?.let {
                updateOrderProducts.invoke(orderNo, it)
            }
        }
    }

    fun doneOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.updateOrder(
                order.copy(
                    status = Order.NEED_PAY
                )
            )
        }
    }

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.updateOrder(
                order.copy(
                    status = Order.CANCEL
                )
            )
        }
    }

    fun putBackOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.updateOrder(
                order.copy(
                    status = Order.ON_PROCESS
                )
            )
        }
    }

    fun payOrder(order: Order, payment: Payment, pay: Long, promo: Promo?, totalPromo: Long?) {
        viewModelScope.launch {
            val newPromo = if (promo != null && totalPromo != null) {
                OrderPromo.newPromo(
                    orderNo = order.orderNo,
                    promo = promo,
                    totalPromo = totalPromo
                )
            } else null

            payOrder.invoke(
                order = order,
                payment = OrderPayment(
                    orderNo = order.orderNo,
                    idPayment = payment.id,
                    pay = pay
                ),
                promo = newPromo
            )
        }
    }

    fun createBucketForEdit(orderNo: String) {
        viewModelScope.launch {
            val order = getOrderDetail(orderNo)
            order?.let {
                val productsOrder = getProductOrder(orderNo).first()

                val newBucket = BucketOrder(
                    time = DateUtils.strToDate(it.order.orderTime).time,
                    products = productsOrder
                )

                _currentBucket.value = newBucket
            }
        }
    }

    fun addProductToBucket(detail: ProductOrderDetail) {
        viewModelScope.launch {
            val newBucket = if (_currentBucket.value.isIdle()) {
                BucketOrder(
                    time = Calendar.getInstance().timeInMillis,
                    products = listOf(detail)
                )
            } else {
                val currentProducts = _currentBucket.value.products!!.toMutableList()
                val existingDetail = currentProducts.findExisting(detail)
                if (existingDetail != null) {
                    currentProducts.remove(existingDetail)
                    currentProducts.add(
                        existingDetail.copy(
                            amount = existingDetail.amount + 1
                        )
                    )
                } else {
                    currentProducts.add(detail)
                }

                _currentBucket.value.copy(
                    products = currentProducts
                )
            }

            _currentBucket.value = newBucket
        }
    }

    fun removeProductFromBucket(detail: ProductOrderDetail) {
        viewModelScope.launch {
            val currentProducts = _currentBucket.value.products?.toMutableList()

            val existingDetail = currentProducts?.findExisting(detail)
            if (existingDetail != null) {
                currentProducts.remove(existingDetail)
            }

            if (currentProducts.isNullOrEmpty()) {
                _currentBucket.value = BucketOrder.idle()
            } else {
                _currentBucket.value = _currentBucket.value.copy(
                    products = currentProducts
                )
            }
        }
    }

    fun decreaseProduct(detail: ProductOrderDetail) {
        viewModelScope.launch {
            val currentProducts = _currentBucket.value.products?.toMutableList()

            val existingDetail = currentProducts?.findExisting(detail)
            if (existingDetail != null) {
                currentProducts.remove(existingDetail)
                if (existingDetail.amount > 1) {
                    currentProducts.add(
                        existingDetail.copy(
                            amount = existingDetail.amount - 1
                        )
                    )
                }
            }

            if (currentProducts.isNullOrEmpty()) {
                _currentBucket.value = BucketOrder.idle()
            } else {
                _currentBucket.value = _currentBucket.value.copy(
                    products = currentProducts
                )
            }
        }
    }

    private fun List<ProductOrderDetail>.findExisting(compare: ProductOrderDetail): ProductOrderDetail? {
        return this.find {
            it.product == compare.product &&
                    it.variants == compare.variants &&
                    it.mixProducts == compare.mixProducts
        }
    }
}
