package com.example.myapplication.view.order_customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.view.viewModel.MainViewModel


@Composable
@ExperimentalComposeUiApi
fun OrderCustomerName(
    mainViewModel: MainViewModel,
    onBackClicked: () -> Unit,
    onNewOrder: (
        customer: Customer,
        isTakeAway: Boolean
    ) -> Unit
) {

    val keyboard = LocalSoftwareKeyboardController.current

    var searchName by remember {
        mutableStateOf("")
    }

    var selectedId by remember {
        mutableStateOf(Customer.ID_ADD)
    }

    val customers by mainViewModel.filterCustomer(searchName)
        .collectAsState(initial = emptyList())

    fun selectedCustomer(customer: Customer) {
        if (customer.isAdd()) {
            mainViewModel.insertCustomers(
                customer,
                onSaved = { id ->
                    selectedId = id
                }
            )
        } else {
            selectedId = if (customer.id == selectedId)
                Customer.ID_ADD
            else
                customer.id
        }
    }

    Scaffold(
        topBar = {
            NameSearchBar(
                value = searchName,
                onBackClicked = onBackClicked,
                onSearch = {
                    searchName = it
                }
            )
        },
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->
        CustomerNames(
            modifier = Modifier
                .padding(padding),
            keyword = searchName,
            selectedId = selectedId,
            customers = customers,
            onClickName = {
                searchName = ""
                selectedCustomer(it)
                keyboard?.hide()
            },
            onChooseDine = { isTakeAway ->
                customers.find { it.id == selectedId }?.let { customer ->
                    onNewOrder(customer, isTakeAway)
                }
            }
        )
    }
}

@Composable
@ExperimentalComposeUiApi
private fun NameSearchBar(
    value: String,
    onBackClicked: () -> Unit,
    onSearch: (keyword: String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = MaterialTheme.colors.primary
            )
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable { onBackClicked() }
                .padding(start = 8.dp)
                .padding(8.dp),
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicEditText(
            modifier = Modifier
                .padding(8.dp),
            value = value,
            placeHolder = stringResource(id = R.string.customer_name),
            onValueChange = onSearch
        )
    }
}

@Composable
private fun CustomerNames(
    modifier: Modifier,
    customers: List<Customer>,
    selectedId: Long,
    keyword: String,
    onClickName: (Customer) -> Unit,
    onChooseDine: (isTakeAway: Boolean) -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            if (keyword.isNotEmpty() && !customers.isAnyMatches(keyword)) {
                item {
                    CustomerItem(
                        keyword = keyword,
                        selectedId = selectedId,
                        onCLickName = { selected ->
                            onClickName(selected)
                        }
                    )
                }
            }
            items(customers) {
                CustomerItem(
                    keyword = keyword,
                    customer = it,
                    selectedId = selectedId,
                    onCLickName = { selected ->
                        onClickName(selected)
                    }
                )
            }
        }

        SelectDineType(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            onChooseDine = onChooseDine
        )
    }
}

private fun List<Customer>.isAnyMatches(keyword: String): Boolean {
    return this.any {
        it.name.lowercase() == keyword
    }
}

@Composable
private fun CustomerItem(
    keyword: String,
    selectedId: Long,
    customer: Customer? = null,
    onCLickName: (Customer) -> Unit
) {

    val isSelected = customer?.id == selectedId

    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCLickName(
                    customer ?: Customer.add(keyword)
                )
            }
            .background(
                color = MaterialTheme.colors.surface
            )
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = customer?.name ?: keyword,
            style = MaterialTheme.typography.body2.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = MaterialTheme.colors.onSurface
        )
        if (customer == null || isSelected) {
            Icon(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(
                    id = if (isSelected) R.drawable.ic_done_all else R.drawable.ic_add
                ),
                tint = MaterialTheme.colors.onSurface,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SelectDineType(
    modifier: Modifier = Modifier,
    onChooseDine: (isTakeAway: Boolean) -> Unit
) {
    Surface(
        modifier = modifier,
        elevation = 4.dp,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        color = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PrimaryButtonView(
                modifier = Modifier.weight(1f),
                buttonText = stringResource(R.string.dine_in),
                onClick = {
                    onChooseDine(false)
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            PrimaryButtonView(
                modifier = Modifier.weight(1f),
                buttonText = stringResource(R.string.take_away),
                onClick = {
                    onChooseDine(true)
                }
            )
        }
    }
}