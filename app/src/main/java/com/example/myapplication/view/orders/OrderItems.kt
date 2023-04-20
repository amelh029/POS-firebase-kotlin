package com.example.myapplication.view.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myapplication.compose.*
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.room.helper.OrderData
import com.example.myapplication.utils.tools.helper.ReportsParameter
import com.example.myapplication.view.ui.GeneralMenus
import com.example.myapplication.view.ui.ModalContent
import com.example.myapplication.view.ui.OrderMenus
import com.example.myapplication.view.viewModel.OrderViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


@Composable
@ExperimentalPagerApi
@ExperimentalMaterialApi
fun OrderItems(
    orderViewModel: OrderViewModel,
    parameters: ReportsParameter,
    defaultTabPage: Int,
    onGeneralMenuClicked: (menu: GeneralMenus) -> Unit,
    onOrderClicked: (orderNo: String) -> Unit,
    onBackClicked: () -> Unit
) {

    if (parameters.isTodayOnly()) {
        val modalContent by remember { mutableStateOf(ModalContent.GENERAL_MENUS) }
        val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()

        ModalBottomSheetLayout(
            sheetState = modalState,
            sheetShape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp
            ),
            sheetContent = {
                when (modalContent) {
                    ModalContent.GENERAL_MENUS -> GeneralMenusView(
                        orderViewModel = orderViewModel,
                        date = parameters.start,
                        onClicked = {
                            if (it == GeneralMenus.ORDERS) {
                                scope.launch {
                                    modalState.hide()
                                }
                            } else {
                                onGeneralMenuClicked(it)
                            }
                        }
                    )

                    else -> {
                        // Do nothing
                    }
                }
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    var shouldShowButton by remember { mutableStateOf(true) }

                    OrderList(
                        viewModel = orderViewModel,
                        parameters = parameters,
                        defaultTabPage = defaultTabPage,
                        onOrderClicked = onOrderClicked,
                        onScrollProgress = {
                            shouldShowButton = !it
                        }
                    )
                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp),
                        visible = shouldShowButton,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        GeneralMenuButtonView(
                            onMenuClicked = {
                                scope.launch {
                                    modalState.show()
                                }
                            }
                        )
                    }
                }
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    BasicTopBar(
                        titleText = parameters.toTitle(),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    OrderList(
                        modifier = Modifier
                            .padding(padding),
                        viewModel = orderViewModel,
                        parameters = parameters,
                        defaultTabPage = defaultTabPage,
                        onOrderClicked = onOrderClicked,
                    )
                }
            )
        }
    }
}

@Composable
@ExperimentalPagerApi
private fun OrderList(
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel,
    parameters: ReportsParameter,
    defaultTabPage: Int,
    onOrderClicked: (orderNo: String) -> Unit,
    onScrollProgress: ((Boolean) -> Unit)? = null
) {
    Column {
        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()
        val menus = OrderMenus.values()

        Surface(
            modifier = modifier,
            elevation = 8.dp,
            color = MaterialTheme.colors.primary
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, it)
                    )
                },
                edgePadding = 16.dp
            ) {
                menus.forEachIndexed { i, menu ->
                    Tab(
                        selected = pagerState.currentPage == i,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(i)
                            }
                        }
                    ) {
                        val badge = viewModel.getOrderBadge(menu, parameters)
                            .collectAsState(initial = null)

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            badge.value?.let {
                                BadgeNumber(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically),
                                    badge = it
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                modifier = Modifier,
                                text = stringResource(id = menu.title),
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                }
            }
        }

        HorizontalPager(
            count = menus.size,
            state = pagerState
        ) { page ->
            OrderContent(
                menu = menus[page],
                parameters = parameters,
                viewModel = viewModel,
                onOrderClicked = onOrderClicked,
                onScrollProgress = onScrollProgress
            )
        }

        if (defaultTabPage != 0) {
            LaunchedEffect(key1 = true) {
                pagerState.animateScrollToPage(defaultTabPage)
            }
        }
    }
}

@Composable
private fun OrderContent(
    menu: OrderMenus,
    parameters: ReportsParameter,
    viewModel: OrderViewModel,
    onOrderClicked: (orderNo: String) -> Unit,
    onScrollProgress: ((Boolean) -> Unit)?
) {

    val orders =
        viewModel.getOrderList(menu.status, parameters).collectAsState(initial = emptyList())

    if (orders.value.isEmpty()) {
        EmptyOrders(menu = menu)
    } else {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .fillMaxSize()
        ) {

            val listState = rememberLazyListState()
            onScrollProgress?.invoke(listState.isScrollInProgress)

            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                state = listState
            ) {
                items(orders.value) {
                    OrderItem(
                        orderData = it,
                        viewModel = viewModel,
                        onOrderClicked = onOrderClicked
                    )
                }

                item { SpaceForFloatingButton() }
            }
        }
    }
}

@Composable
private fun EmptyOrders(
    menu: OrderMenus
) {
    val (id, text) = when (menu) {
        OrderMenus.CURRENT_ORDER -> Pair(
            R.drawable.ic_on_process,
            R.string.yeay_all_order_are_done
        )

        OrderMenus.NOT_PAY_YET -> Pair(
            R.drawable.ic_get_payment,
            R.string.all_orders_are_paid_already
        )

        OrderMenus.CANCELED -> Pair(
            R.drawable.ic_happy_face,
            R.string.yeay_there_no_cancel_order
        )

        OrderMenus.DONE -> Pair(
            R.drawable.ic_cancel_order,
            R.string.no_done_order_yet_spirit_find_some_customer_today
        )
    }

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .fillMaxSize()
    ) {
        BasicEmptyList(imageId = id, text = text)
    }
}

@Composable
private fun OrderItem(
    orderData: OrderData,
    viewModel: OrderViewModel,
    onOrderClicked: (orderNo: String) -> Unit
) {

    var orderProducts by remember {
        mutableStateOf(
            OrderWithProduct(
                order = orderData
            )
        )
    }

    LaunchedEffect(key1 = orderData) {
        viewModel.getProductOrder(orderData.order.orderNo)
            .collectLatest {
                orderProducts = orderProducts.copy(
                    products = it
                )
            }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .clickable {
                onOrderClicked(orderProducts.order.order.orderNo)
            }
            .background(
                color = MaterialTheme.colors.surface
            )
            .padding(16.dp)
    ) {

        val (name, total, dine, icon) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            text = orderProducts.order.customer.name,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface
        )
        Text(
            modifier = Modifier
                .constrainAs(total) {
                    top.linkTo(name.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                },
            text = "Rp. ${orderProducts.grandTotal.thousand()}",
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colors.onSurface
        )
        Text(
            modifier = Modifier
                .constrainAs(dine) {
                    linkTo(top = total.top, bottom = total.bottom)
                    start.linkTo(total.end, margin = 16.dp)
                },
            text = stringResource(
                id = if (orderProducts.order.order.isTakeAway) R.string.take_away else R.string.dine_in
            ),
            style = MaterialTheme.typography.overline.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colors.onSurface
        )
        Icon(
            modifier = Modifier
                .constrainAs(icon) {
                    end.linkTo(parent.end)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
            painter = painterResource(id = R.drawable.ic_dimsum_50dp),
            tint = MaterialTheme.colors.background,
            contentDescription = null
        )
    }
}
