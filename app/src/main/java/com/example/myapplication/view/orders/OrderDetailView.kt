package com.example.myapplication.view.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.example.myapplication.R
import com.example.myapplication.compose.BasicAlertDialog
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.utils.config.rupiahToK
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.ui.OrderMenus
import com.example.myapplication.view.viewModel.OrderViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker


@Composable
fun OrderDetailView(
    orderNo: String,
    orderViewModel: OrderViewModel,
    timePicker: MaterialTimePicker.Builder,
    datePicker: MaterialDatePicker.Builder<Long>,
    fragmentManager: FragmentManager,
    onBackClicked: () -> Unit,
    onButtonClicked: (OrderButtonType, OrderWithProduct?) -> Unit,
    onProductsClicked: () -> Unit
) {

    val order = orderViewModel.getOrderData(orderNo).collectAsState(initial = null)
    val products = orderViewModel.getProductOrder(orderNo).collectAsState(initial = emptyList())
    val orderWithProducts = order.value?.run {
        OrderWithProduct(
            order = this,
            products = products.value
        )
    }

    var alertDoneState by remember { mutableStateOf(false) }
    var alertCancelState by remember { mutableStateOf(false) }
    var alertPaymentState by remember { mutableStateOf(false) }
    var alertPutBackState by remember { mutableStateOf(false) }
    var alertReplaceDate by remember { mutableStateOf(false) }

    var selectedDateTime by remember { mutableStateOf(orderWithProducts?.order?.order?.orderTime) }

    fun selectTime() {
        if (orderWithProducts != null && selectedDateTime != null) {
            val hourMinute = DateUtils.strToHourAndMinute(orderWithProducts.order.order.orderTime)
            val picker = timePicker.setHour(hourMinute.first)
                .setMinute(hourMinute.second)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()
            picker.addOnPositiveButtonClickListener {
                selectedDateTime = DateUtils.strDateTimeReplaceTime(
                    dateTime = selectedDateTime!!,
                    hour = picker.hour,
                    minute = picker.minute
                )
                picker.dismiss()
                alertReplaceDate = true
            }
            picker.show(fragmentManager, "")
        }
    }

    fun selectDate() {
        orderWithProducts?.let { order ->
            val date = DateUtils.strToDate(order.order.order.orderTime).time
            val picker =
                datePicker.setSelection(date)
                    .build()
            picker.addOnPositiveButtonClickListener {
                val newDate = DateUtils.millisToDate(
                    millis = it,
                    isWithTime = true
                )
                selectedDateTime = newDate
                picker.dismiss()
                selectTime()
            }
            picker.show(fragmentManager, "")
        }
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                titleText = orderWithProducts?.order?.customer?.name ?: "",
                onBackClicked = onBackClicked
            )
        },
        content = { padding ->
            orderWithProducts?.let {
                Details(
                    modifier = Modifier
                        .padding(padding),
                    orderWithProduct = it,
                    onHeaderClicked = {
                        selectDate()
                    },
                    onProductsClicked = onProductsClicked,
                    onMenuClicked = { buttonType ->
                        when (buttonType) {
                            OrderButtonType.PRINT, OrderButtonType.QUEUE -> onButtonClicked(
                                buttonType,
                                orderWithProducts
                            )

                            OrderButtonType.PAYMENT -> {
                                alertPaymentState = true
                            }

                            OrderButtonType.DONE -> {
                                alertDoneState = true
                            }

                            OrderButtonType.CANCEL -> {
                                alertCancelState = true
                            }

                            OrderButtonType.PUT_BACK -> {
                                alertPutBackState = true
                            }
                        }
                    }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )

    if (alertPaymentState) {
        BasicAlertDialog(
            titleText = stringResource(R.string.pay_the_order),
            descText = stringResource(R.string.are_you_sure_will_pay_this_order),
            positiveAction = {
                onButtonClicked(OrderButtonType.PAYMENT, null)
                alertPaymentState = false
            },
            positiveText = stringResource(R.string.yes),
            negativeAction = {
                alertPaymentState = false
            },
            negativeText = stringResource(R.string.no)
        )
    }

    if (alertCancelState) {
        BasicAlertDialog(
            titleText = stringResource(R.string.cancel_the_order),
            descText = stringResource(R.string.are_you_sure_will_cancel_this_order),
            positiveAction = {
                orderWithProducts?.order?.order?.let {
                    orderViewModel.cancelOrder(it)
                }
                onButtonClicked(OrderButtonType.CANCEL, null)
                alertCancelState = false
            },
            positiveText = stringResource(R.string.yes),
            negativeAction = {
                alertCancelState = false
            },
            negativeText = stringResource(R.string.no)
        )
    }

    if (alertDoneState) {
        BasicAlertDialog(
            titleText = stringResource(R.string.done_the_order),
            descText = stringResource(R.string.are_you_sure_will_done_this_order),
            positiveAction = {
                orderWithProducts?.order?.order?.let {
                    orderViewModel.doneOrder(it)
                }
                alertDoneState = false
                onButtonClicked(OrderButtonType.DONE, null)
            },
            positiveText = stringResource(R.string.yes),
            negativeAction = {
                alertDoneState = false
            },
            negativeText = stringResource(R.string.no)
        )
    }
    if (alertPutBackState) {
        BasicAlertDialog(
            titleText = stringResource(R.string.put_back_order),
            descText = stringResource(R.string.are_you_sure_will_put_back_this_order),
            positiveAction = {
                orderWithProducts?.order?.order?.let {
                    orderViewModel.putBackOrder(it)
                }
                alertPutBackState = false
                onButtonClicked(OrderButtonType.PUT_BACK, null)
            },
            positiveText = stringResource(R.string.yes),
            negativeAction = {
                alertPutBackState = false
            },
            negativeText = stringResource(R.string.no)
        )
    }
    if (alertReplaceDate && selectedDateTime != null) {
        val date = DateUtils.convertDateFromDb(selectedDateTime, DateUtils.DATE_WITH_TIME_FORMAT)
        BasicAlertDialog(
            titleText = stringResource(R.string.change_order_date),
            descText = stringResource(R.string.are_you_sure_change_order_date_to, date),
            positiveAction = {
                orderWithProducts?.order?.order?.let {
                    orderViewModel.updateOrder(
                        it.copy(
                            orderTime = selectedDateTime!!
                        )
                    )
                }
                alertReplaceDate = false
                onBackClicked()
            },
            positiveText = stringResource(R.string.yes),
            negativeAction = {
                alertReplaceDate = false
            },
            negativeText = stringResource(R.string.no)
        )
    }
}

@Composable
private fun Details(
    modifier: Modifier = Modifier,
    orderWithProduct: OrderWithProduct,
    onMenuClicked: (OrderButtonType) -> Unit,
    onHeaderClicked: () -> Unit,
    onProductsClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            item {
                OrderHeader(
                    order = orderWithProduct.order.order,
                    onHeaderClicked = onHeaderClicked
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .run {
                            return@run if (orderWithProduct.order.order.isEditable()) {
                                clickable {
                                    onProductsClicked()
                                }
                            } else {
                                this
                            }
                        }
                ) {
                    orderWithProduct.products.forEachIndexed { i, product ->
                        ProductOrder(
                            number = (i + 1).toString(),
                            productOrderDetail = product
                        )
                    }
                }
            }

            item {
                OrderFooter(orderWithProduct = orderWithProduct)
            }
        }
        orderWithProduct.order.order.statusToOrderMenu()?.let {
            ButtonBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                orderStatus = it,
                onMenuClicked = onMenuClicked
            )
        }
    }
}

@Composable
private fun OrderHeader(
    order: Order,
    onHeaderClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.surface
            )
            .clickable {
                onHeaderClicked()
            }
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = order.timeString,
                style = MaterialTheme.typography.body2
            )
            Text(
                text = stringResource(
                    id = if (order.isTakeAway) R.string.take_away else R.string.dine_in
                ),
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = order.getQueueNumber(),
            style = MaterialTheme.typography.h6
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ProductOrder(
    number: String,
    productOrderDetail: ProductOrderDetail
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.surface
            )
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = "$number.",
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = "x${productOrderDetail.amount}",
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = productOrderDetail.product?.name ?: "",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = productOrderDetail.generateVariantsString(),
                style = MaterialTheme.typography.overline
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = productOrderDetail.product?.sellPrice?.thousand()?.rupiahToK() ?: "",
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = productOrderDetail.totalPrice().thousand().rupiahToK(),
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun OrderFooter(
    orderWithProduct: OrderWithProduct
) {
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = "${orderWithProduct.totalItem} items",
            style = MaterialTheme.typography.body2
        )
        Column(
            horizontalAlignment = Alignment.End
        ) {
            orderWithProduct.order.promo?.let {
                Text(
                    text = "Rp. ${orderWithProduct.grandTotal.thousand()}",
                    style = MaterialTheme.typography.body1
                )
                Row {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rp. ${orderWithProduct.totalPromo.thousand()}",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
            Text(
                text = "Rp. ${orderWithProduct.grandTotalWithPromo.thousand()}",
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            orderWithProduct.order.payment?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ButtonBottomBar(
    modifier: Modifier = Modifier,
    orderStatus: OrderMenus,
    onMenuClicked: (OrderButtonType) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 10.dp,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                when (orderStatus) {
                    OrderMenus.CURRENT_ORDER -> OrderMenuButton(onMenuClicked = onMenuClicked)
                    OrderMenus.NOT_PAY_YET -> NeedPayMenuButton(onMenuClicked = onMenuClicked)
                    OrderMenus.DONE -> DoneMenuButton(onMenuClicked = onMenuClicked)
                    OrderMenus.CANCELED -> CanceledMenuButton(onMenuClicked = onMenuClicked)
                }
            }
        }
    }
}

@Composable
private fun RowScope.OrderMenuButton(
    onMenuClicked: (OrderButtonType) -> Unit
) {
    OrderDetailButton(
        buttonType = OrderButtonType.QUEUE,
        onMenuClicked = onMenuClicked
    )
    Spacer(modifier = Modifier.width(16.dp))
    OrderDetailButton(
        buttonType = OrderButtonType.PRINT,
        onMenuClicked = onMenuClicked
    )
    Spacer(modifier = Modifier.width(16.dp))
    OrderDetailButton(
        buttonType = OrderButtonType.DONE,
        isMain = true,
        onMenuClicked = onMenuClicked
    )
    Spacer(modifier = Modifier.width(16.dp))
    OrderDetailButton(
        buttonType = OrderButtonType.PAYMENT,
        onMenuClicked = onMenuClicked
    )
    Spacer(modifier = Modifier.width(16.dp))
    OrderDetailButton(
        buttonType = OrderButtonType.CANCEL,
        onMenuClicked = onMenuClicked
    )
}

@Composable
private fun RowScope.NeedPayMenuButton(
    onMenuClicked: (OrderButtonType) -> Unit
) {
    OrderDetailButton(
        buttonType = OrderButtonType.PRINT,
        onMenuClicked = onMenuClicked
    )
    Spacer(modifier = Modifier.width(16.dp))
    OrderDetailButton(
        buttonType = OrderButtonType.PAYMENT,
        isMain = true,
        onMenuClicked = onMenuClicked
    )
    Spacer(modifier = Modifier.width(16.dp))
    OrderDetailButton(
        buttonType = OrderButtonType.CANCEL,
        onMenuClicked = onMenuClicked
    )
}

@Composable
private fun RowScope.DoneMenuButton(
    onMenuClicked: (OrderButtonType) -> Unit
) {
    OrderDetailButton(
        buttonType = OrderButtonType.PRINT,
        isMain = true,
        onMenuClicked = onMenuClicked
    )
}

@Composable
private fun RowScope.CanceledMenuButton(
    onMenuClicked: (OrderButtonType) -> Unit
) {
    OrderDetailButton(
        buttonType = OrderButtonType.PUT_BACK,
        isMain = true,
        onMenuClicked = onMenuClicked
    )
}

@Composable
private fun RowScope.OrderDetailButton(
    buttonType: OrderButtonType,
    isMain: Boolean = false,
    onMenuClicked: (OrderButtonType) -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .background(
                color = if (isMain) MaterialTheme.colors.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable {
                onMenuClicked(buttonType)
            }
            .padding(8.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(if (isMain) 40.dp else 25.dp),
            painter = painterResource(id = buttonType.icon),
            tint = if (isMain) Color.White else MaterialTheme.colors.primary,
            contentDescription = null
        )
    }
}
