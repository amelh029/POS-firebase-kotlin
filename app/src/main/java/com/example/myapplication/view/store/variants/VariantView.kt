package com.example.myapplication.view.store.variants

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.*
import com.example.myapplication.data.source.local.entity.room.bridge.VariantProduct
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import com.example.myapplication.view.ui.ModalContent
import com.example.myapplication.view.viewModel.ProductViewModel
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun VariantView (
    productViewModel: ProductViewModel,
    idProduct: Long? = null,
    onBackClicked: () -> Unit
) {

    if (idProduct != null) {
        VariantProductView(
            productViewModel = productViewModel,
            idProduct = idProduct,
            onBackClicked = onBackClicked
        )
    } else {
        VariantMasterView(productViewModel = productViewModel, onBackClicked = onBackClicked)
    }
}

@Composable
private fun VariantProductView(
    productViewModel: ProductViewModel,
    idProduct: Long,
    onBackClicked: () -> Unit
) {
    val product = productViewModel.getProduct(idProduct).collectAsState(initial = null)

    Scaffold(
        topBar = {
            val title = "${stringResource(R.string.variants)} - ${product.value?.name}"
            BasicTopBar(
                titleText = title,
                onBackClicked = onBackClicked
            )
        },
        content = { padding ->
            VariantsContent(
                modifier = Modifier
                    .padding(padding),
                productViewModel = productViewModel,
                product = product.value
            )
        }
    )
}

@Composable
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
private fun VariantMasterView(
    productViewModel: ProductViewModel,
    onBackClicked: () -> Unit
) {
    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    if (modalState.currentValue == ModalBottomSheetValue.Hidden) {
        LocalSoftwareKeyboardController.current?.hide()
    }

    var sheetContent by remember {
        mutableStateOf(ModalContent.VARIANT_DETAIL)
    }

    var selectedVariant by remember {
        mutableStateOf<Variant?>(null)
    }

    var selectedOption by remember {
        mutableStateOf<Pair<Variant, VariantOption?>?>(null)
    }

    ModalBottomSheetLayout(
        sheetState = modalState,
        sheetShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        sheetContent = {
            when (sheetContent) {
                ModalContent.VARIANT_DETAIL -> VariantDetail(
                    variant = selectedVariant,
                    onVariantSubmitted = {
                        if (it.isAdd())
                            productViewModel.insertVariant(it)
                        else
                            productViewModel.updateVariant(it)

                        scope.launch {
                            modalState.hide()
                        }
                        selectedVariant = null
                    }
                )

                ModalContent.VARIANT_OPTION_DETAIL -> VariantOptionDetail(
                    variant = selectedOption?.first,
                    option = selectedOption?.second,
                    onOptionSubmitted = {
                        if (it.isAdd())
                            productViewModel.insertVariantOption(it)
                        else
                            productViewModel.updateVariantOption(it)

                        scope.launch {
                            modalState.hide()
                        }
                        selectedVariant = null
                    }
                )

                else -> {
                    // Do nothing
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    BasicTopBar(
                        titleText = stringResource(R.string.variants),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    VariantsContent(
                        modifier = Modifier
                            .padding(padding),
                        productViewModel = productViewModel,
                        onAddVariantClicked = {
                            selectedVariant = null
                            scope.launch {
                                sheetContent = ModalContent.VARIANT_DETAIL
                                modalState.show()
                            }
                        },
                        onVariantClicked = {
                            selectedVariant = it
                            scope.launch {
                                sheetContent = ModalContent.VARIANT_DETAIL
                                modalState.show()
                            }
                        },
                        onAddOptionClicked = {
                            selectedOption = Pair(
                                it,
                                null
                            )
                            scope.launch {
                                sheetContent = ModalContent.VARIANT_OPTION_DETAIL
                                modalState.show()
                            }
                        },
                        onOptionClicked = { variant, option ->
                            selectedOption = Pair(
                                variant,
                                option
                            )
                            scope.launch {
                                sheetContent = ModalContent.VARIANT_OPTION_DETAIL
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
@ExperimentalMaterialApi
private fun VariantDetail(
    variant: Variant?,
    onVariantSubmitted: (variant: Variant) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isMust by remember { mutableStateOf(false) }
    var isSingleOption by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = variant) {
        name = variant?.name ?: ""
        isMust = variant?.isMust ?: false
        isSingleOption = variant?.isSingleOption() ?: true
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        BasicEditText(
            value = name,
            placeHolder = stringResource(R.string.variant_name),
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Checkbox(
                checked = isMust,
                onCheckedChange = {
                    isMust = it
                }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = stringResource(id = R.string.must_choice),
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            BasicRadioButton(
                isSelected = isSingleOption,
                text = stringResource(R.string.single_option),
                onClicked = {
                    isSingleOption = true
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            BasicRadioButton(
                isSelected = !isSingleOption,
                text = stringResource(R.string.multiple_option),
                onClicked = {
                    isSingleOption = false
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(
                id = if (variant == null) R.string.adding else R.string.save
            ),
            onClick = {
                val newVariant = variant?.copy(
                    name = name,
                    type = if (isSingleOption) Variant.ONE_OPTION else Variant.MULTIPLE_OPTION,
                    isMust = isMust,
                ) ?: Variant(
                    name = name,
                    type = if (isSingleOption) Variant.ONE_OPTION else Variant.MULTIPLE_OPTION,
                    isMust = isMust,
                    isMix = false
                )
                onVariantSubmitted(newVariant)
                name = ""
                isMust = false
                isSingleOption = true
            }
        )
    }
}

@Composable
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
private fun VariantOptionDetail(
    variant: Variant?,
    option: VariantOption?,
    onOptionSubmitted: (VariantOption) -> Unit
) {
    var name by remember { mutableStateOf("") }

    LaunchedEffect(key1 = option) {
        name = option?.name ?: ""
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = variant?.name ?: "",
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicEditText(
            value = name,
            placeHolder = stringResource(R.string.option_name),
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButtonView(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(
                id = if (option == null) R.string.adding else R.string.save
            ),
            onClick = {
                val newOption = option?.copy(
                    name = name
                ) ?: VariantOption(
                    idVariant = variant?.id ?: 0L,
                    name = name,
                    desc = "",
                    isCount = false,
                    isActive = true
                )
                onOptionSubmitted(newOption)
                name = ""
            }
        )
    }
}

@Composable
private fun VariantsContent(
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel,
    product: Product? = null,
    onAddVariantClicked: (() -> Unit)? = null,
    onAddOptionClicked: ((Variant) -> Unit)? = null,
    onVariantClicked: ((Variant) -> Unit)? = null,
    onOptionClicked: ((Variant, VariantOption) -> Unit)? = null
) {

    val isProductVariants = product != null

    val selectedProductOptions = productViewModel.getVariantsProductById(product?.id ?: 0L)
        .collectAsState(initial = emptyList())
    val variants = productViewModel.variants.collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        val listState = rememberLazyListState()
        var expandedItem by remember { mutableStateOf<Variant?>(null) }

        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter),
            state = listState
        ) {
            items(variants.value) {

                val query = VariantOption.getFilter(it.id, VariantOption.ALL)
                val options =
                    productViewModel.getVariantOptions(query).collectAsState(initial = emptyList())

                VariantItem(
                    isProductVariant = isProductVariants,
                    variant = it,
                    options = options.value,
                    selectedProductOptions = selectedProductOptions.value.filter { variantProduct ->
                        variantProduct.idVariant == it.id
                    },
                    isExpanded = expandedItem == it,
                    onVariantClicked = {
                        onVariantClicked?.invoke(it)
                    },
                    onAddOptionClicked = { addVariant ->
                        onAddOptionClicked?.invoke(addVariant)
                    },
                    onOptionClicked = { optVariant, option ->
                        onOptionClicked?.invoke(optVariant, option)
                    },
                    onOptionMasterSwitched = { option ->
                        productViewModel.updateVariantOption(option)
                    },
                    onOptionProductSwitched = { option, variantProduct ->
                        if (variantProduct != null) {
                            productViewModel.removeVariantProduct(variantProduct)
                        } else {
                            val newVariantProduct = VariantProduct(
                                idVariant = option.idVariant,
                                idVariantOption = option.id,
                                idProduct = product?.id ?: 0L
                            )
                            productViewModel.insertVariantProduct(newVariantProduct)
                        }
                    },
                    onExpand = { variant ->
                        expandedItem = if (expandedItem != variant) variant else null
                    }
                )
            }

            item { SpaceForFloatingButton() }
        }

        if (!isProductVariants) {
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
                    onAddClicked = {
                        onAddVariantClicked?.invoke()
                    }
                )
            }
        }
    }
}

@Composable
private fun VariantItem(
    isProductVariant: Boolean,
    variant: Variant,
    options: List<VariantOption>,
    selectedProductOptions: List<VariantProduct>,
    isExpanded: Boolean,
    onVariantClicked: () -> Unit,
    onAddOptionClicked: (Variant) -> Unit,
    onOptionClicked: (Variant, VariantOption) -> Unit,
    onOptionMasterSwitched: (option: VariantOption) -> Unit,
    onOptionProductSwitched: (option: VariantOption, variantProduct: VariantProduct?) -> Unit,
    onExpand: (Variant) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                return@run if (!isProductVariant) {
                    clickable {
                        onVariantClicked()
                    }
                } else {
                    this
                }
            }
            .background(color = MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = variant.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            val descText = when {
                isProductVariant -> {
                    stringResource(
                        id = R.string.option_selected,
                        selectedProductOptions.size.toString()
                    )
                }

                else -> {
                    "${
                        if (variant.isMust == true) stringResource(id = R.string.must_choice) else stringResource(
                            id = R.string.optional_choice
                        )
                    }, ${
                        if (variant.isSingleOption()) stringResource(id = R.string.single_option) else stringResource(
                            id = R.string.multiple_option
                        )
                    }"
                }
            }
            Text(
                text = descText,
                style = MaterialTheme.typography.body1
            )
        }
        Icon(
            modifier = Modifier
                .clickable {
                    onExpand(variant)
                }
                .align(Alignment.CenterVertically)
                .padding(16.dp),
            painter = painterResource(
                id = if (isExpanded) R.drawable.ic_expand_less_24 else R.drawable.ic_expand_more_24
            ),
            tint = MaterialTheme.colors.onSurface,
            contentDescription = null
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    if (isExpanded) {
        if (!isProductVariant) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.surface)
                    .clickable {
                        onAddOptionClicked(variant)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = stringResource(R.string.add_new_option),
                    style = MaterialTheme.typography.body2.copy(
                        fontStyle = FontStyle.Italic
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
        options.forEach {

            val variantProduct = selectedProductOptions.find { variantProduct ->
                variantProduct.idVariantOption == it.id
            }

            OptionItem(
                isProductVariant = isProductVariant,
                optionName = it.name,
                isOptionActive = if (isProductVariant) {
                    variantProduct != null
                } else {
                    it.isActive
                },
                onOptionClicked = {
                    onOptionClicked(
                        variant,
                        it
                    )
                },
                onOptionSwitch = { isActive ->
                    if (isProductVariant) {
                        onOptionProductSwitched(
                            it,
                            variantProduct
                        )
                    } else {
                        onOptionMasterSwitched(
                            it.copy(
                                isActive = isActive
                            )
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun OptionItem(
    isProductVariant: Boolean,
    optionName: String,
    isOptionActive: Boolean,
    onOptionClicked: () -> Unit,
    onOptionSwitch: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .run {
                return@run if (!isProductVariant) {
                    clickable {
                        onOptionClicked()
                    }
                } else {
                    this
                }
            }
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            text = optionName,
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Switch(
            checked = isOptionActive,
            onCheckedChange = {
                onOptionSwitch(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.primaryVariant
            )
        )
    }
}
