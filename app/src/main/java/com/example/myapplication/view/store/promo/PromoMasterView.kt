package com.example.myapplication.view.store.promo

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.*
import com.example.myapplication.data.source.local.entity.room.master.Promo
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.ui.ThousandAndSuggestionVisualTransformation
import com.example.myapplication.view.viewModel.MainViewModel
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi


fun PromoMasterView(
    mainViewModel: MainViewModel,
    onBackClicked: () -> Unit
) {

    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var selectedPromo by remember { mutableStateOf<Promo?>(null) }

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
            PromoDetail(
                promo = selectedPromo,
                onSubmitPromo = {
                    scope.launch {
                        if (it.isNewPromo()) {
                            mainViewModel.insertPromo(it)
                        } else {
                            mainViewModel.updatePromo(it)
                        }
                        selectedPromo = null
                        modalState.hide()
                    }
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    BasicTopBar(
                        titleText = stringResource(R.string.promos),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    PromoContent(
                        modifier = Modifier
                            .padding(padding),
                        mainViewModel = mainViewModel,
                        onPromoClicked = {
                            scope.launch {
                                selectedPromo = it
                                modalState.show()
                            }
                        },
                        onAddClicked = {
                            scope.launch {
                                selectedPromo = null
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
private fun PromoDetail(
    promo: Promo?,
    onSubmitPromo: (Promo) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isCash by remember { mutableStateOf(true) }
    var isDefineCashValue by remember { mutableStateOf(false) }
    var definedCash by remember { mutableStateOf<Int?>(null) }
    var percentage by remember { mutableStateOf<Int?>(null) }
    val isValueCheck = if (isCash) {
        if (isDefineCashValue) definedCash != null && definedCash != 0 else true
    } else {
        percentage != null && percentage != 0 && percentage!! <= 100
    }

    LaunchedEffect(key1 = promo) {
        name = promo?.name ?: ""
        desc = promo?.desc ?: ""
        isCash = promo?.isCash ?: false
        isDefineCashValue = isCash && promo?.value != null
        definedCash = if (isCash) promo?.value else null
        percentage = if (isCash) null else promo?.value
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        BasicEditText(
            value = name,
            placeHolder = stringResource(R.string.promo_name),
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
        Row {
            BasicRadioButton(
                isSelected = isCash,
                text = stringResource(id = R.string.promo_cash),
                onClicked = {
                    isCash = true
                }
            )
            BasicRadioButton(
                isSelected = !isCash,
                text = stringResource(id = R.string.promo_percentage),
                onClicked = {
                    isCash = false
                }
            )
        }
        if (isCash) {
            Spacer(modifier = Modifier.height(8.dp))
            BasicCheckBox(
                titleText = stringResource(R.string.define_cash_value),
                isChecked = isDefineCashValue,
                onCheckedChange = {
                    isDefineCashValue = it
                }
            )
            if (isDefineCashValue) {
                Spacer(modifier = Modifier.height(8.dp))
                BasicEditText(
                    value = if (definedCash == 0) "" else definedCash.toString(),
                    placeHolder = stringResource(R.string.total_cash_promo),
                    keyboardType = KeyboardType.Number,
                    visualTransformation = ThousandAndSuggestionVisualTransformation(false),
                    onValueChange = {
                        definedCash = it.toIntOrNull() ?: 0
                    }
                )
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            BasicEditText(
                value = percentage?.toString() ?: "",
                placeHolder = stringResource(R.string.percentage_1_100),
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    percentage = it.toIntOrNull()
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            isEnabled = name.isNotEmpty() && isValueCheck,
            buttonText = stringResource(
                id = if (promo == null) R.string.adding else R.string.save
            ),
            onClick = {
                if (name.isNotEmpty()) {
                    onSubmitPromo(
                        promo?.copy(
                            name = name,
                            desc = desc,
                            isCash = isCash,
                            value = if (isCash) definedCash else percentage
                        ) ?: Promo.createNewPromo(
                            name = name,
                            desc = desc,
                            isCash = isCash,
                            value = if (isCash) definedCash else percentage
                        )
                    )
                    name = ""
                    desc = ""
                    isCash = false
                    isDefineCashValue = false
                    definedCash = null
                    percentage = null
                }
            }
        )
    }
}

@Composable
private fun PromoContent(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    onPromoClicked: (Promo) -> Unit,
    onAddClicked: () -> Unit
) {

    val promos = mainViewModel.getPromos(Promo.Status.ALL).collectAsState(initial = emptyList())

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
            items(promos.value) {
                PromoItem(
                    promo = it,
                    onPromoClicked = onPromoClicked,
                    onPromoSwitched = { isActive ->
                        mainViewModel.updatePromo(
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
private fun PromoItem(
    promo: Promo,
    onPromoClicked: (Promo) -> Unit,
    onPromoSwitched: (isActive: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPromoClicked(promo)
            }
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = promo.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    id = if (promo.isCash) R.string.promo_cash else R.string.promo_percentage
                ),
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (promo.isCash) {
                    if (promo.value != null) {
                        "Rp. ${promo.value?.toLong()?.thousand()}"
                    } else {
                        stringResource(R.string.manual_input)
                    }
                } else {
                    "${promo.value}%"
                },
                style = MaterialTheme.typography.body2
            )
        }
        Switch(
            checked = promo.isActive,
            onCheckedChange = onPromoSwitched,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.primaryVariant
            )
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
