package com.example.myapplication.view.store.payments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.*
import com.example.myapplication.data.source.local.entity.room.master.Payment
import com.example.myapplication.view.viewModel.MainViewModel
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun PaymentMasterView (
    mainViewModel: MainViewModel,
    onBackClicked: () -> Unit
) {

    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var selectedPayment by remember { mutableStateOf<Payment?>(null) }

    if (modalState.currentValue == ModalBottomSheetValue.Hidden) {
        LocalSoftwareKeyboardController.current?.hide()
    }

    ModalBottomSheetLayout(
        sheetState = modalState,
        sheetShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        sheetContent = {
            PaymentDetail(
                payment = selectedPayment,
                onSubmitPayment = {
                    scope.launch {
                        if (it.isNewPayment()) {
                            mainViewModel.insertPayment(it)
                        } else {
                            mainViewModel.updatePayment(it)
                        }
                        selectedPayment = null
                        modalState.hide()
                    }
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    BasicTopBar(
                        titleText = stringResource(R.string.payments),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    PaymentContent(
                        modifier = Modifier
                            .padding(padding),
                        mainViewModel = mainViewModel,
                        onPaymentClicked = {
                            scope.launch {
                                selectedPayment = it
                                modalState.show()
                            }
                        },
                        onAddClicked = {
                            scope.launch {
                                selectedPayment = null
                                modalState.show()
                            }
                        }
                    )
                }
            )
        }
    )
}

@Composable
@ExperimentalComposeUiApi
private fun PaymentDetail(
    payment: Payment?,
    onSubmitPayment: (Payment) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isCash by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = payment) {
        name = payment?.name ?: ""
        desc = payment?.desc ?: ""
        isCash = payment?.isCash ?: false
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        BasicEditText(
            value = name,
            placeHolder = stringResource(R.string.payment_name),
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(
            value = desc,
            placeHolder = stringResource(R.string.deskripsi_optional),
            onValueChange = {
                desc = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicCheckBox(
            isChecked = isCash,
            titleText = stringResource(R.string.cash_payment),
            onCheckedChange = {
                isCash = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(
                id = if (payment == null) R.string.adding else R.string.save
            ),
            onClick = {
                if (name.isNotEmpty()) {
                    onSubmitPayment(
                        payment?.copy(
                            name = name,
                            desc = desc,
                            isCash = isCash
                        ) ?: Payment.createNewPayment(
                            name = name,
                            desc = desc,
                            isCash = isCash
                        )
                    )
                    name = ""
                    desc = ""
                    isCash = false
                }
            }
        )
    }
}

@Composable
private fun PaymentContent(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    onPaymentClicked: (Payment) -> Unit,
    onAddClicked: () -> Unit
) {

    val query = Payment.filter(Payment.ALL)
    val payments = mainViewModel.getPayments(query).collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter),
            state = listState
        ) {
            items(payments.value) {
                PaymentItem(
                    payment = it,
                    onPaymentClicked = onPaymentClicked,
                    onPaymentSwitched = { isActive ->
                        mainViewModel.updatePayment(
                            it.copy(
                                isActive = isActive
                            )
                        )
                    }
                )
            }

            item { SpaceForFloatingButton() }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            visible = !listState.isScrollInProgress,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BasicAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                onAddClicked = onAddClicked
            )
        }
    }
}

@Composable
private fun PaymentItem(
    payment: Payment,
    onPaymentClicked: (Payment) -> Unit,
    onPaymentSwitched: (isActive: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPaymentClicked(payment)
            }
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = payment.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    id = if (payment.isCash) R.string.cash else R.string.non_cash
                ),
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = payment.desc,
                style = MaterialTheme.typography.body2
            )
        }
        Switch(
            checked = payment.isActive,
            onCheckedChange = onPaymentSwitched,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.primaryVariant
            )
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
