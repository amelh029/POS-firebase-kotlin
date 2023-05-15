package com.example.myapplication.view.store.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.R
import com.example.myapplication.compose.BasicAddButton
import com.example.myapplication.compose.SpaceForFloatingButton
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.viewModel.ProductViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@Composable
@ExperimentalPagerApi
fun ProductsMaster(
    productViewModel: ProductViewModel,
    onBackClicked: () -> Unit,
    onItemClicked: (Product) -> Unit,
    onVariantClicked: (Product) -> Unit,
    onAddProductClicked: () -> Unit
) {

    val pagerState = rememberPagerState()
    val query = Category.getFilter(Category.ALL)
    val categories = productViewModel.getCategories(query).collectAsState(initial = emptyList())

    Box {

        var shouldShowButton by remember { mutableStateOf(true) }

        Scaffold(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize(),
            topBar = {
                ProductHeader(
                    pagerState = pagerState,
                    categories = categories.value,
                    onBackClicked = onBackClicked
                )
            },
            content = { padding ->
                HorizontalPager(
                    modifier = Modifier
                        .padding(padding),
                    count = categories.value.size,
                    state = pagerState
                ) { page ->
                    ProductItems(
                        productViewModel = productViewModel,
                        category = categories.value[page],
                        onItemClicked = onItemClicked,
                        onVariantClicked = onVariantClicked,
                        onScrollInProgress = {
                            shouldShowButton = !it
                        }
                    )
                }
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
            BasicAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                onAddClicked = onAddProductClicked
            )
        }
    }
}

@Composable
@ExperimentalPagerApi
private fun ProductHeader(
    pagerState: PagerState,
    categories: List<Category>,
    onBackClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 8.dp,
        color = MaterialTheme.colors.primary
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
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
            if (categories.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = {
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, it)
                        )
                    },
                    edgePadding = 16.dp
                ) {
                    categories.forEachIndexed { i, category ->
                        Tab(
                            selected = pagerState.currentPage == i,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(i)
                                }
                            }
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = category.name,
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItems(
    productViewModel: ProductViewModel,
    category: Category,
    onItemClicked: (Product) -> Unit,
    onVariantClicked: (Product) -> Unit,
    onScrollInProgress: (Boolean) -> Unit
) {

    val products =
        productViewModel.getProductWithCategories(category.id).collectAsState(initial = emptyList())

    val listState = rememberLazyListState()
    onScrollInProgress(listState.isScrollInProgress)

    LazyColumn(state = listState) {
        items(products.value) {
            ProductItem(
                productViewModel = productViewModel,
                product = it.product,
                onItemClicked = onItemClicked,
                onVariantClicked = {
                    onVariantClicked(it.product)
                }
            )
        }

        item { SpaceForFloatingButton() }
    }
}

@Composable
private fun ProductItem(
    productViewModel: ProductViewModel,
    product: Product,
    onItemClicked: (Product) -> Unit,
    onVariantClicked: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .clickable { onItemClicked(product) }
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {

        val (content, switch, variant) = createRefs()

        Column(
            modifier = Modifier
                .constrainAs(content) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = parent.start, end = variant.start, endMargin = 8.dp)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth()
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.desc,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rp. ${product.sellPrice.thousand()}",
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Switch(
            modifier = Modifier
                .constrainAs(switch) {
                    linkTo(top = parent.top, bottom = variant.top)
                    end.linkTo(parent.end)
                },
            checked = product.isActive,
            onCheckedChange = {
                productViewModel.updateProduct(
                    product.copy(
                        isActive = it
                    )
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.primaryVariant
            )
        )

        Row(
            modifier = Modifier
                .clickable {
                    onVariantClicked()
                }
                .constrainAs(variant) {
                    linkTo(top = switch.bottom, bottom = parent.bottom)
                    end.linkTo(parent.end)
                },
        ) {

            val variantsCount =
                productViewModel.getProductVariantCount(product.id).collectAsState(
                    initial = 0
                )

            val text = if (variantsCount.value == 0) {
                stringResource(id = R.string.select_variant)
            } else {
                "${variantsCount.value} " + stringResource(id = R.string.variant)
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = text,
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                tint = MaterialTheme.colors.onSurface,
                contentDescription = null
            )
        }
    }
}
