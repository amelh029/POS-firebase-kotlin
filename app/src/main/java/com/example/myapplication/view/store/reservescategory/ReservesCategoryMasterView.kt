package com.example.myapplication.view.store.reservescategory

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
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicAddButton
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.compose.SpaceForFloatingButton
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import com.example.myapplication.view.viewModel.ReservesViewModel
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun ReservesCategoryMasterView(
    reservesViewModel: ReservesViewModel,
    onBackClicked: () -> Unit
) {

    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var selectedReservesCategory by remember { mutableStateOf<ReservesCategory?>(null) }

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
            ReservesCategoryDetail(
                reservesCategory = selectedReservesCategory,
                onSubmitReservesCategory = {
                    scope.launch {
                        if (it.isNewReservesCategory()) {
                            reservesViewModel.insertReservesCategory(it)
                        } else {
                            reservesViewModel.updateReservesCategory(it)
                        }
                        selectedReservesCategory = null
                        modalState.hide()
                    }
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    BasicTopBar(
                        titleText = stringResource(id = R.string.ResourceCategoryData),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    ReservesCategoryContent(
                        modifier = Modifier
                            .padding(padding),
                        reservesViewModel = reservesViewModel,
                        onReservesCategoryClicked = {
                            scope.launch {
                                selectedReservesCategory = it
                                modalState.show()
                            }
                        },
                        onAddClicked = {
                            scope.launch {
                                selectedReservesCategory = null
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
private fun ReservesCategoryDetail(
    reservesCategory: ReservesCategory?,
    onSubmitReservesCategory: (ReservesCategory) -> Unit
) {
    var namecatres by remember {
        mutableStateOf("")
    }
    var desccatres by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = reservesCategory) {
        namecatres = reservesCategory?.name ?: ""
        desccatres = reservesCategory?.desc ?: ""
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        BasicEditText(
            value = namecatres,
            placeHolder = stringResource(R.string.Name_Reserves_Category),
            onValueChange = {
                namecatres = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(value = desccatres,
            placeHolder = stringResource(R.string.deskripsi_optional),
            onValueChange = {
                desccatres = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(
                id = if(reservesCategory == null) R.string.adding else R.string.save
        ),
            onClick = {
                if (namecatres.isNotEmpty()) {
                    onSubmitReservesCategory(
                        reservesCategory?.copy(
                            name = namecatres,
                            desc = desccatres
                        ) ?: ReservesCategory.createNewReservesCategory(
                            name_cat_res = namecatres,
                            desc_cat_res = desccatres
                        )
                    )
                    namecatres = ""
                    desccatres = ""
                }
            }
        )

    }

}

@Composable
private fun ReservesCategoryContent(
    modifier: Modifier = Modifier,
    reservesViewModel: ReservesViewModel,
    onReservesCategoryClicked: (ReservesCategory) -> Unit,
    onAddClicked: () -> Unit
){
    val query = ReservesCategory.getFilterCarRees(ReservesCategory.ALL)
    val reservesCategory = reservesViewModel.getReservesCategories(query).collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .fillMaxSize()
    ){

        val listState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter),
            state = listState
        ) {
            items(reservesCategory.value) {
                ReservesCategoryItem(
                    reservesCategory = it,
                    onReservesCategoryClicked = onReservesCategoryClicked,
                    onReservesCategorySwitched = { isActive ->
                        reservesViewModel.updateReservesCategory(
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
private fun ReservesCategoryItem(
    reservesCategory: ReservesCategory,
    onReservesCategoryClicked: (ReservesCategory) -> Unit,
    onReservesCategorySwitched: (isActive: Boolean) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onReservesCategoryClicked(reservesCategory)
            }
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = reservesCategory.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reservesCategory.desc,
                style = MaterialTheme.typography.body2
            )
        }
        Switch(
            checked = reservesCategory.isActive,
            onCheckedChange = onReservesCategorySwitched,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.primaryVariant
            )
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}