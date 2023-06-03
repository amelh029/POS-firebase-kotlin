package com.example.myapplication.view.store.Reserves

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.compose.basicDropdown
import com.example.myapplication.data.source.local.entity.room.helper.ReservesWithCategory
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import com.example.myapplication.view.ui.ThousandAndSuggestionVisualTransformation
import com.example.myapplication.view.viewModel.ReservesViewModel


@Composable
@ExperimentalComposeUiApi
fun ReservesDetailMaster(
    reservesViewModel: ReservesViewModel,
    reservesId: Long,
    onBackClicked: () -> Unit,
    onCreateNewReserves: (Reserves) -> Unit
) {
    val isNewReserves = reservesId == 0L

    val reserves = reservesViewModel.getReservesWithCategory(reservesId)
        .collectAsState(initial = null)
    var isEditMode by remember { mutableStateOf(isNewReserves) }
    LaunchedEffect(key1 = isNewReserves) {
        isEditMode = isNewReserves
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                titleText = stringResource(R.string.resource_data),
                onBackClicked = onBackClicked,
                endIcon = if (isNewReserves) null else R.drawable.ic_edit_24,
                endAction = {
                    isEditMode = !isEditMode
                }
            )
        },
        content = { padding ->
            DetailContent(
                modifier = Modifier.padding(padding),
                reservesViewModel = reservesViewModel,
                reserves = reserves.value,
                isEditMode = isEditMode,
                onSubmitEditReserve = {
                    if (isNewReserves) {
                        onCreateNewReserves(it)
                    } else {
                        reservesViewModel.updateReserves(it)
                        isEditMode = false
                    }
                }
            )
        }
    )
}

@Composable
@ExperimentalComposeUiApi
private fun DetailContent(
    modifier: Modifier = Modifier,
    reservesViewModel: ReservesViewModel,
    reserves: ReservesWithCategory?,
    isEditMode: Boolean,
    onSubmitEditReserve: (Reserves) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val query = ReservesCategory.getFilterCarRees(ReservesCategory.ALL)
        val resCategories =
            reservesViewModel.getReservesCategories(query).collectAsState(initial = emptyList())

        var categoryExpanded by remember {
            mutableStateOf(false)
        }
        var selectedCategory by remember {
            mutableStateOf(reserves?.reservesCategory)
        }
        var isError by remember { mutableStateOf(false) }

        var name by remember { mutableStateOf("") }
        var meas by remember { mutableStateOf("") }
        var quant by remember { mutableStateOf(0L) }

        LaunchedEffect(key1 = "$reserves $isEditMode") {
            name = reserves?.reserves?.name ?: ""
            meas = reserves?.reserves?.meassure ?: ""
            quant = reserves?.reserves?.quantity ?: 0L
            selectedCategory = reserves?.reservesCategory
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            LazyColumn {
                basicDropdown(
                    isExpanded = categoryExpanded,
                    title = R.string.select_category,
                    selectedItem = selectedCategory?.name,
                    items = resCategories.value,
                    isEnable = isEditMode,
                    onHeaderClicked = {
                        categoryExpanded = !categoryExpanded
                    },
                    onSelectedItem = {
                        isError = false
                        selectedCategory = it as ReservesCategory
                        categoryExpanded = false
                    }
                )

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colors.surface)
                            .padding(16.dp)
                    ) {
                        BasicEditText(
                            value = name,
                            placeHolder = stringResource(R.string.name),
                            isEnabled = isEditMode,
                            onValueChange = {
                                isError = false
                                name = it
                            }
                        )
                        if (isError && name.isEmpty()) {
                            ErrorText()
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicEditText(
                            value = meas,
                            placeHolder = stringResource(R.string.meassure),
                            isEnabled = isEditMode,
                            onValueChange = {
                                isError = false
                                meas = it
                            }
                        )
                        if (isError && meas.isEmpty()) {
                            ErrorText()
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val quantity = if (quant == 0L) "" else quant.toString()
                        BasicEditText(
                            value = quantity,
                            placeHolder = stringResource(R.string.Quantity),
                            isEnabled = isEditMode,
                            visualTransformation = ThousandAndSuggestionVisualTransformation(false),
                            keyboardType = KeyboardType.Number,
                            onValueChange = {
                                isError = false
                                quant = it.toLongOrNull() ?: 0L
                            }
                        )
                        if (isError && quantity.isEmpty()) {
                            ErrorText()
                        }
                    }
                }

            }
        }

        fun checkData() {
            if (
                name.isNotEmpty() && meas.isNotEmpty() && quant != 0L && selectedCategory != null
            ) {
                selectedCategory?.let { reservesCategory ->
                    if (reserves != null) {
                        onSubmitEditReserve(
                            reserves.reserves.copy(
                                name = name,
                                meassure = meas,
                                quantity = quant,
                                reservesCategory = reservesCategory.id
                            )
                        )
                    } else {
                        onSubmitEditReserve(
                            Reserves.createNewReserves(
                                name = name,
                                measure = meas,
                                quantity = quant,
                                reservesCategory = reservesCategory.id
                            )
                        )
                    }
                }
            } else {
                isError = true
            }
        }

        if (isEditMode) {
            EditButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                isEnable = !isError,
                onSaveButtonClicked = {
                    checkData()
                }
            )
        }
    }
}

@Composable
private fun ErrorText() {
    Text(
        text = stringResource(R.string.can_not_be_empty),
        style = MaterialTheme.typography.overline,
        color = Color.Red
    )
}

@Composable
private fun EditButton(
    modifier: Modifier = Modifier,
    isEnable: Boolean,
    onSaveButtonClicked: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        elevation = 8.dp,
        color = MaterialTheme.colors.surface
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            PrimaryButtonView(
                modifier = Modifier
                    .fillMaxWidth(),
                isEnabled = isEnable,
                buttonText = stringResource(R.string.save),
                onClick = onSaveButtonClicked
            )
        }
    }
}