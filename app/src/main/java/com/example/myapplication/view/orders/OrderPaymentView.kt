package com.example.myapplication.view.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.compose.basicDropdown
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.entity.room.master.Payment
import com.example.myapplication.data.source.local.entity.room.master.Promo
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.ui.ThousandAndSuggestionVisualTransformation
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first


@Composable
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
fun OrderPaymentView(
    orderNo: String,
    mainViewModel: MainViewModel,
    orderViewModel: OrderViewModel,
    onBackClicked: () -> Unit,
    onPayClicked: (order: Order, payment: Payment, pay: Long, promo: Promo?, total: Long?) -> Unit
) {

    var orderWithProducts by remember {
        mutableStateOf<OrderWithProduct?>(null)
    }

    LaunchedEffect(key1 = orderNo) {
        orderViewModel.getOrderDetail(orderNo)?.let {
            val products = orderViewModel.getProductOrder(orderNo).first()
            orderWithProducts = OrderWithProduct(
                order = it,
                products = products
            )
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
                PaymentContent(
                    modifier = Modifier
                        .padding(padding),
                    mainViewModel = mainViewModel,
                    orderWithProduct = it,
                    onPayClicked = onPayClicked
                )
            }
        }
    )
}

@Composable
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
private fun PaymentContent(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    orderWithProduct: OrderWithProduct,
    onPayClicked: (order: Order, payment: Payment, pay: Long, promo: Promo?, total: Long?) -> Unit
) {

    val keyboard = LocalSoftwareKeyboardController.current

    val query = Payment.filter(Payment.ACTIVE)
    val payments = mainViewModel.getPayments(query).collectAsState(initial = emptyList())
    val promos = mainViewModel.getPromos(Promo.Status.ACTIVE).collectAsState(initial = emptyList())
    val cashSuggestions = mainViewModel.cashSuggestions.collectAsState(initial = null)

    var paymentExpanded by remember {
        mutableStateOf(false)
    }

    var promoExpanded by remember {
        mutableStateOf(false)
    }

    var selectedPayment by remember {
        mutableStateOf<Payment?>(null)
    }

    var selectedPromo by remember {
        mutableStateOf<Promo?>(null)
    }

    var cashAmount by remember {
        mutableStateOf(Pair(0L, false))
    }

    var manualInputPromo by remember {
        mutableStateOf<Long?>(null)
    }

    val totalPromo = selectedPromo?.calculatePromo(
        total = orderWithProduct.grandTotal,
        manualInput = manualInputPromo
    ) ?: 0L
    val grandTotal = orderWithProduct.grandTotal - totalPromo

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {

            if (promos.value.isNotEmpty()) {
                basicDropdown(
                    isExpanded = promoExpanded,
                    title = R.string.select_promo,
                    selectedItem = selectedPromo?.name,
                    items = promos.value,
                    onHeaderClicked = {
                        promoExpanded = !promoExpanded
                    },
                    onSelectedItem = {
                        selectedPromo = if (it == selectedPromo) {
                            null
                        } else {
                            it as Promo
                        }
                        promoExpanded = false
                    }
                )
                if (selectedPromo?.isManualInput() == true) {
                    item {
                        BasicEditText(
                            modifier = Modifier
                                .background(color = MaterialTheme.colors.surface)
                                .padding(16.dp),
                            value = manualInputPromo?.toString() ?: "",
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                            visualTransformation = ThousandAndSuggestionVisualTransformation(cashAmount.second),
                            placeHolder = stringResource(R.string.promo_amount),
                            onValueChange = {
                                val amount = it.toLongOrNull() ?: 0L
                                manualInputPromo = amount
                            },
                            onAction = {
                                keyboard?.hide()
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.surface)
                        .padding(16.dp)
                ) {
                    selectedPromo?.let {
                        Text(
                            text = "Rp. ${orderWithProduct.grandTotal.thousand()} - ${it.name}",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = "Rp. ${grandTotal.thousand()}",
                        style = MaterialTheme.typography.h3
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            basicDropdown(
                isExpanded = paymentExpanded,
                title = R.string.select_payment,
                selectedItem = selectedPayment?.name,
                items = payments.value,
                onHeaderClicked = {
                    paymentExpanded = !paymentExpanded
                },
                onSelectedItem = {
                    selectedPayment = it as Payment
                    paymentExpanded = false
                }
            )

            selectedPayment?.let { payment ->
                if (payment.isCash) {
                    item {
                        PaymentCashOption(
                            totalAmount = grandTotal,
                            cashAmount = cashAmount,
                            cashSuggestions = cashSuggestions.value,
                            onAmountChange = {
                                mainViewModel.addCashInput(
                                    it.first,
                                    grandTotal
                                )
                                cashAmount = it
                            }
                        )
                    }
                }
            }
        }

        PaymentFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            selectedPayment = selectedPayment,
            isEnough = cashAmount.first >= grandTotal,
            onPayClicked = {
                selectedPayment?.let {
                    onPayClicked(
                        orderWithProduct.order.order,
                        it,
                        cashAmount.first,
                        selectedPromo,
                        totalPromo
                    )
                }
            }
        )
    }
}

@Composable
@ExperimentalComposeUiApi
private fun PaymentCashOption(
    totalAmount: Long,
    cashAmount: Pair<Long, Boolean>,
    cashSuggestions: List<Long>?,
    onAmountChange: (Pair<Long, Boolean>) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        val maxWidth = maxWidth

        Column {

            val newAmount = if (cashAmount.first == 0L) "" else cashAmount.first.toString()
            BasicEditText(
                value = newAmount,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                visualTransformation = ThousandAndSuggestionVisualTransformation(cashAmount.second),
                placeHolder = stringResource(R.string.cash_amount),
                onValueChange = {
                    val amount = it.toLongOrNull() ?: 0L
                    onAmountChange(Pair(amount, false))
                },
                onAction = {
                    keyboardController?.hide()
                }
            )

            if (!cashSuggestions.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                LazyHorizontalGrid(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .height(35.dp),
                    rows = GridCells.Adaptive(maxWidth)
                ) {
                    items(cashSuggestions) { suggestion ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                                .clickable {
                                    onAmountChange(Pair(suggestion, true))
                                }
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                text = suggestion.thousand(),
                                style = MaterialTheme.typography.overline
                            )
                        }
                    }
                }
            }

            Row {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                    text = stringResource(R.string.cash_change),
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.width(16.dp))
                val change = cashAmount.first - totalAmount
                Text(
                    text = "Rp. ${change.thousand()}",
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

@Composable
private fun PaymentFooter(
    modifier: Modifier = Modifier,
    selectedPayment: Payment?,
    isEnough: Boolean,
    onPayClicked: () -> Unit
) {

    var errorText by remember {
        mutableStateOf<Int?>(null)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 10.dp,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            errorText?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(it),
                    style = MaterialTheme.typography.body1.copy(
                        textAlign = TextAlign.Center
                    ),
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            PrimaryButtonView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                buttonText = stringResource(R.string.pay),
                onClick = {
                    if (selectedPayment == null) {
                        errorText = R.string.please_select_payment
                    }
                    if (selectedPayment?.isCash == true && !isEnough) {
                        errorText = R.string.pay_amount_can_not_less_than_total_pay
                    } else {
                        onPayClicked()
                    }
                }
            )
        }
    }
}
