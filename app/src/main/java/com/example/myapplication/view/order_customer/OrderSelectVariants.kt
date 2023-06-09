package com.example.myapplication.view.order_customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.helper.VariantWithOptions
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.viewModel.ProductViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun OrderSelectVariants(
    viewModel: ProductViewModel,
    productId: Long,
    onBackClicked: () -> Unit,
    onAddToBucketClicked: (detail: ProductOrderDetail) -> Unit
) {

    val variants = viewModel.getProductVariantOptions(productId)
        .collectAsState(initial = null)
    var productOrderDetail by remember {
        mutableStateOf(ProductOrderDetail.empty())
    }

    LaunchedEffect(key1 = productId) {
        val product = viewModel.getProduct(productId).first()
        productOrderDetail = productOrderDetail.copy(
            product = product,
            amount = 1
        )
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val titleText =
                "${productOrderDetail.product?.name} - Rp. ${productOrderDetail.product?.sellPrice?.thousand()}"
            BasicTopBar(
                titleText = titleText,
                onBackClicked = onBackClicked
            )
        },
        content = { padding ->
            SelectVariantsContent(
                modifier = Modifier
                    .padding(padding),
                variants = variants.value,
                productOrderDetail = productOrderDetail,
                onOptionSelected = { prevOption, option, isSelected ->
                    scope.launch {
                        productOrderDetail = productOrderDetail.copy(
                            variants = if (isSelected) {
                                productOrderDetail.addOption(option, prevOption)
                            } else {
                                productOrderDetail.removeOption(option)
                            }
                        )
                    }
                },
                onAmountClicked = {
                    val newAmount = productOrderDetail.amount.run {
                        return@run if (it) {
                            this + 1
                        } else {
                            if (this == 1) this else this - 1
                        }
                    }

                    productOrderDetail = productOrderDetail.copy(
                        amount = newAmount
                    )
                },
                onAddToBucketClicked = {
                    onAddToBucketClicked(productOrderDetail)
                }
            )
        }
    )
}

@Composable
private fun SelectVariantsContent(
    modifier: Modifier = Modifier,
    variants: List<VariantWithOptions>?,
    productOrderDetail: ProductOrderDetail,
    onOptionSelected: (prevOption: VariantOption?, option: VariantOption, isSelected: Boolean) -> Unit,
    onAmountClicked: (isAdd: Boolean) -> Unit,
    onAddToBucketClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
        ) {
            variants?.let {
                items(it) { variant ->
                    if (variant.variant.isSingleOption())
                        VariantItemSingleOption(
                            variantWithOption = variant,
                            onOptionSelected = onOptionSelected
                        )
                    else
                        VariantItemManyOption(
                            variantWithOption = variant,
                            onOptionSelected = { option, isSelected ->
                                onOptionSelected(null, option, isSelected)
                            }
                        )
                }
            }

            item { SpaceForAddCartButton() }
        }

        val isAddEnabled = productOrderDetail.isAllMustVariantSelected(variants ?: emptyList())
        AddToCartBottom(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            productOrderDetail = productOrderDetail,
            isAddEnabled = isAddEnabled,
            onAmountClicked = onAmountClicked,
            onAddToBucketClicked = onAddToBucketClicked
        )
    }
}

@Composable
private fun SpaceForAddCartButton() {
    Spacer(modifier = Modifier.height(140.dp))
}

@Composable
private fun VariantItemManyOption(
    variantWithOption: VariantWithOptions,
    onOptionSelected: (options: VariantOption, isSelected: Boolean) -> Unit
) {

    var currentSelectedVariants by remember {
        mutableStateOf<List<VariantOption>?>(null)
    }

    val isMust = variantWithOption.variant.isMust ?: false

    Spacer(modifier = Modifier.height(4.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        VariantTitle(
            titleText = variantWithOption.variant.name,
            isMust = isMust,
            isError = isMust && currentSelectedVariants == null
        )
        variantWithOption.options
            .forEach { option ->
                VariantOptionManyOption(
                    option = option,
                    onOptionSelected = {
                        currentSelectedVariants = if (it) {
                            currentSelectedVariants.add(option)
                        } else {
                            currentSelectedVariants.remove(option)
                        }
                        onOptionSelected(option, it)
                    }
                )
            }
    }
}

private fun List<VariantOption>?.add(option: VariantOption): List<VariantOption> {
    return this?.toMutableList()?.apply {
        add(option)
    }?.toList() ?: listOf(option)
}

private fun List<VariantOption>?.remove(option: VariantOption): List<VariantOption>? {
    return if (this != null) {
        val existingOption = this.find { it == option }
        if (existingOption == null) {
            this
        } else {
            val newVariants = this.toMutableList().apply {
                remove(existingOption)
            }.toList()

            return newVariants.ifEmpty { null }
        }
    } else {
        null
    }
}

@Composable
private fun VariantTitle(
    titleText: String,
    isMust: Boolean,
    isError: Boolean
) {
    Row {
        Text(
            text = titleText,
            style = MaterialTheme.typography.body1
        )
        if (isError) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_error_outline_24),
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))

    val descText = if (isMust) {
        stringResource(R.string.must_choice)
    } else {
        stringResource(R.string.optional_choice)
    }
    Text(
        text = descText,
        style = MaterialTheme.typography.overline
    )
    Spacer(modifier = Modifier.height(8.dp))
    Spacer(
        modifier = Modifier
            .height(0.5.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colors.onPrimary)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun VariantOptionManyOption(
    option: VariantOption,
    onOptionSelected: (isSelected: Boolean) -> Unit
) {

    var isChecked by remember {
        mutableStateOf(false)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (name, checkBox) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(name) {
                    linkTo(start = parent.start, end = checkBox.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth(),
            text = option.name,
            style = MaterialTheme.typography.body2
        )
        Row(
            modifier = Modifier
                .constrainAs(checkBox) {
                    end.linkTo(parent.end)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
        ) {
            if (!option.isActive) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.out_of_stock),
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    onOptionSelected(it)
                    isChecked = it
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.onSurface
                ),
                enabled = option.isActive
            )
        }
    }
}

@Composable
private fun VariantItemSingleOption(
    variantWithOption: VariantWithOptions,
    onOptionSelected: (prevOption: VariantOption?, option: VariantOption, isSelected: Boolean) -> Unit
) {

    var selectedOption by remember {
        mutableStateOf<VariantOption?>(null)
    }

    val isMust = variantWithOption.variant.isMust ?: false

    Spacer(modifier = Modifier.height(4.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        VariantTitle(
            titleText = variantWithOption.variant.name,
            isMust = isMust,
            isError = isMust && selectedOption == null
        )
        variantWithOption.options
            .forEach { option ->
                VariantOptionSingleOption(
                    option = option,
                    isSelected = selectedOption == option,
                    onOptionSelected = {
                        val isSelected = selectedOption != option
                        onOptionSelected(selectedOption, option, isSelected)
                        selectedOption = if (selectedOption == option) {
                            null
                        } else {
                            option
                        }

                    }
                )
            }
    }
}

@Composable
private fun VariantOptionSingleOption(
    option: VariantOption,
    isSelected: Boolean,
    onOptionSelected: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (name, radio) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(name) {
                    linkTo(start = parent.start, end = radio.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth(),
            text = option.name,
            style = MaterialTheme.typography.body2
        )
        Row(
            modifier = Modifier
                .constrainAs(radio) {
                    end.linkTo(parent.end)
                    linkTo(top = parent.top, bottom = parent.bottom)
                },
        ) {
            if (!option.isActive) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.out_of_stock),
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            RadioButton(
                selected = isSelected,
                enabled = option.isActive,
                onClick = onOptionSelected,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colors.onSurface
                )
            )
        }
    }
}

@Composable
private fun AddToCartBottom(
    modifier: Modifier = Modifier,
    productOrderDetail: ProductOrderDetail,
    isAddEnabled: Boolean,
    onAmountClicked: (isAdd: Boolean) -> Unit,
    onAddToBucketClicked: () -> Unit
) {
    Surface(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = 10.dp,
        color = MaterialTheme.colors.surface
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(16.dp)
        ) {
            val (text, qty, addCartBtn) = createRefs()
            Text(
                modifier = Modifier
                    .constrainAs(text) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                text = stringResource(R.string.amount)
            )
            Row(
                modifier = Modifier
                    .constrainAs(qty) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .clickable {
                            onAmountClicked(false)
                        },
                    painter = painterResource(id = R.drawable.ic_remove_circle),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = productOrderDetail.amount.toString(),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Black
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    modifier = Modifier
                        .clickable {
                            onAmountClicked(true)
                        },
                    painter = painterResource(id = R.drawable.ic_add_circle),
                    contentDescription = null,
                )
            }
            PrimaryButtonView(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .constrainAs(addCartBtn) {
                        linkTo(start = parent.start, end = parent.end)
                        linkTo(
                            bottom = parent.bottom,
                            top = qty.bottom,
                            topMargin = 16.dp,
                        )
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(),
                isEnabled = isAddEnabled,
                buttonText = stringResource(if (isAddEnabled) R.string.adding else R.string.variant_must_selected),
                onClick = onAddToBucketClicked
            )
        }
    }
}
