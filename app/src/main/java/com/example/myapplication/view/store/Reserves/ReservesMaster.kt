package com.example.myapplication.view.store.Reserves

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.R
import com.example.myapplication.compose.BasicAddButton
import com.example.myapplication.compose.SpaceForFloatingButton
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.viewModel.ReservesViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


@Composable
@ExperimentalPagerApi
fun ReservesMaster(
    reserveViewModel: ReservesViewModel,
    onBackClicked: () -> Unit,
    onAddReservesClicked: () -> Unit,
    onItemClicked: (Reserves) -> Unit

) {
    val pagerState = rememberPagerState()
    val query = ReservesCategory.getFilterCarRees(ReservesCategory.ALL)
    val resCategories = reserveViewModel.getReservesCategories(query).collectAsState(initial = emptyList())

    Box {
        var shouldShowButton by remember {
            mutableStateOf(true)
        }

        Scaffold(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize(),
            topBar = {
                ReservesHeader(
                    pagerState = pagerState,
                    reservesCategory = resCategories.value,
                    onBackClicked = onBackClicked
                )
            },
            content = { padding ->
                HorizontalPager(
                    modifier = Modifier
                        .padding(padding),
                    count = resCategories.value.size,
                    state = pagerState
                ) { page ->
                    ReservesItems(
                        reservesViewModel = reserveViewModel,
                        reservesCategory = resCategories.value[page],
                        onItemClicked = onItemClicked,
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
                onAddClicked = onAddReservesClicked
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalPagerApi
private fun ReservesHeader(
    pagerState: PagerState,
    reservesCategory: List<ReservesCategory>,
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
            if (reservesCategory.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = {
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, it)
                        )
                    },
                    edgePadding = 16.dp
                ) {
                    reservesCategory.forEachIndexed { i, reservesCategory ->
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
                                    text = reservesCategory.name,
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
private fun ReservesItems(
    reservesViewModel: ReservesViewModel,
    reservesCategory: ReservesCategory,
    onItemClicked: (Reserves) -> Unit,
    onScrollInProgress: (Boolean) -> Unit
) {

    val reserves =
        reservesViewModel.getReservesWithCategories(reservesCategory.id)
            .collectAsState(initial = emptyList())

    val listState = rememberLazyListState()
    onScrollInProgress(listState.isScrollInProgress)

    LazyColumn(state = listState) {
        items(reserves.value) {
            ReservesItem(
                reservesViewModel = reservesViewModel,
                reserves = it.reserves,
                onItemClicked = onItemClicked
            )
        }

        item { SpaceForFloatingButton() }
    }
}

@Composable
private fun ReservesItem(
    reservesViewModel: ReservesViewModel,
    reserves: Reserves,
    onItemClicked: (Reserves) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .clickable { onItemClicked(reserves) }
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
                text = reserves.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reserves.meassure,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reserves.quantity.thousand(),
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold
                )
            )

        }
/*
       Switch(
            modifier = Modifier
                .constrainAs(switch) {
                    linkTo(top = parent.top, bottom = variant.top)
                    end.linkTo(parent.end)
                },
            checked = reserves.isActive,
            onCheckedChange = {
                reservesViewModel.updateReserves(
                    reserves.copy(
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

                }
                .constrainAs(variant) {
                    linkTo(top = switch.bottom, bottom = parent.bottom)
                    end.linkTo(parent.end)
                },
        ) {


            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = "TEXT",
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                tint = MaterialTheme.colors.onSurface,
                contentDescription = null
            )
        }*/
    }
}
