package com.example.myapplication.view.store.Reserves

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicAddButton
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.compose.SpaceForFloatingButton
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.view.viewModel.ProductViewModel
import com.example.myapplication.view.viewModel.ReservesViewModel
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun ReservesMaster(
    reserveViewModel: ReservesViewModel,
    onBackClicked: () -> Unit
    //onAddReservesClicked: () -> Unit
    ) {
    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var selectedReserves by remember {mutableStateOf<Reserves?>(null)}
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
            ReserveDetail(
                reserves = selectedReserves,
                isEditMode = true

            ) {
                scope.launch {
                    if (it.isNewReserves()) {
                        reserveViewModel.insertReserves(it)
                    } else {
                        reserveViewModel.updatetReserves(it)
                    }
                    selectedReserves = null
                    modalState.hide()
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    BasicTopBar(
                        titleText = stringResource(id = R.string.Resource_name),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    ReservesContent(
                        modifier = Modifier
                            .padding(padding),
                        reserveViewModel = reserveViewModel,
                        onReservesClicked ={
                            scope.launch {
                                selectedReserves = it
                                modalState.show()
                            }
                        },
                        onAddClicked = {
                            scope.launch {
                                selectedReserves = null
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
private fun ReserveDetail(
    reserves: Reserves?,
    isEditMode: Boolean,
    onSubmitReserves: (Reserves) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var meas by remember { mutableStateOf("") }
    var quant by remember { mutableStateOf(0L) }

    var isError by remember{ mutableStateOf(false) }

    LaunchedEffect(key1 = reserves) {
        name = reserves?.name ?: ""
        meas = reserves?.meassure ?: ""
        quant = reserves?.quantity ?: 0L

    }
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        BasicEditText(value = name,
            placeHolder = stringResource(R.string.resource_name),
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(value = meas,
            placeHolder = stringResource(R.string.meassure),
            onValueChange = {
                meas = it
            })
        BasicEditText(value = if(quant == 0L)"" else quant.toString(),
            placeHolder = stringResource(R.string.Quantity),
            isEnabled = isEditMode,
            keyboardType = KeyboardType.Number,
            onValueChange = {
                isError = false
                quant = it.toLongOrNull() ?: 0L
            })
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(
                id = if (reserves == null) R.string.adding else R.string.save
            ),
            onClick = {
                if (name.isNotEmpty()) {
                    onSubmitReserves(
                        reserves?.copy(
                            name = name,
                            meassure = meas,
                            quantity = quant

                        ) ?: Reserves.createNewReserves(
                            name = name,
                            measure = meas,
                            quantity = quant
                        )
                    )
                    name = ""
                    meas = ""
                    quant = 0L
                }
            }
        )
    }

}

@Composable
private fun ReservesContent(
    modifier: Modifier = Modifier,
    reserveViewModel: ReservesViewModel,
    onReservesClicked: (Reserves) -> Unit,
    onAddClicked: () -> Unit
) {
    val query = Reserves.getFilter(Reserves.ALL)
    val reserves = reserveViewModel.getReserves(query).collectAsState(initial = emptyList())
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
            items(reserves.value) {
                ReservesItem(
                    reserves = it,
                    onReservesClicked = onReservesClicked,
                    onReservesSwitched = {isActive ->
                        reserveViewModel.updatetReserves(
                            it.copy(
                                isActive = isActive
                            )
                        )
                    }
                )

            }
            item{ SpaceForFloatingButton()}
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
private fun ReservesItem(
    reserves: Reserves,
    onReservesClicked: (Reserves) -> Unit,
    onReservesSwitched: (isActive: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onReservesClicked(reserves)
            }
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = reserves.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reserves.quantity.toString(),
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reserves.meassure,
                style = MaterialTheme.typography.body2
            )
        }


    }
    Spacer(modifier = Modifier.height(4.dp))
}
