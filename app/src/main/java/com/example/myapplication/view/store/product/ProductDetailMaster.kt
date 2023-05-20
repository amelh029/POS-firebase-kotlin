package com.example.myapplication.view.store.product

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.compose.basicDropdown
import com.example.myapplication.data.source.local.entity.helper.VariantWithOptions
import com.example.myapplication.data.source.local.entity.room.helper.ProductWithCategory
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.utils.config.thousand
import com.example.myapplication.view.ui.ThousandAndSuggestionVisualTransformation
import com.example.myapplication.view.viewModel.ProductViewModel

@Composable
@ExperimentalComposeUiApi
fun ProductDetailMaster(
    productViewModel: ProductViewModel,
    productId: Long,
    onVariantClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onCreateNewProduct: (Product) -> Unit
) {

    val isNewProduct = productId == 0L

    val product = productViewModel.getProductWithCategory(productId)
        .collectAsState(initial = null)
    var isEditMode by remember { mutableStateOf(isNewProduct) }
    LaunchedEffect(key1 = isNewProduct) {
        isEditMode = isNewProduct
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                titleText = stringResource(R.string.product_detail),
                onBackClicked = onBackClicked,
                endIcon = if (isNewProduct) null else R.drawable.ic_edit_24,
                endAction = {
                    isEditMode = !isEditMode
                }
            )
        },
        content = { padding ->
            DetailContent(
                modifier = Modifier.padding(padding),
                productViewModel = productViewModel,
                product = product.value,
                isEditMode = isEditMode,
                onVariantClicked = onVariantClicked,
                onSubmitEditProduct = {
                    if (isNewProduct) {
                        onCreateNewProduct(it)
                    } else {
                        productViewModel.updateProduct(it)
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
    productViewModel: ProductViewModel,
    product: ProductWithCategory?,
    isEditMode: Boolean,
    onVariantClicked: () -> Unit,
    onSubmitEditProduct: (Product) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        val query = Category.getFilter(Category.ALL)
        val categories =
            productViewModel.getCategories(query).collectAsState(initial = emptyList())

        var categoryExpanded by remember {
            mutableStateOf(false)
        }

        var selectedCategory by remember {
            mutableStateOf(product?.category)
        }

        var isError by remember { mutableStateOf(false) }

        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var sellPrice by remember { mutableStateOf(0L) }

        LaunchedEffect(key1 = "$product $isEditMode") {
            name = product?.product?.name ?: ""
            desc = product?.product?.desc ?: ""
            sellPrice = product?.product?.sellPrice ?: 0L
            selectedCategory = product?.category
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
                    items = categories.value,
                    isEnable = isEditMode,
                    onHeaderClicked = {
                        categoryExpanded = !categoryExpanded
                    },
                    onSelectedItem = {
                        isError = false
                        selectedCategory = it as Category
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
                        if (isError && name.isEmpty()) { ErrorText() }
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicEditText(
                            value = desc,
                            placeHolder = stringResource(R.string.description),
                            isEnabled = isEditMode,
                            onValueChange = {
                                isError = false
                                desc = it
                            }
                        )
                        if (isError && desc.isEmpty()) { ErrorText() }

                        Spacer(modifier = Modifier.height(8.dp))
                        val price = if (sellPrice == 0L) "" else sellPrice.toString()
                        BasicEditText(
                            value = if (isEditMode) price else "Rp. ${price.toLongOrNull()?.thousand()}",
                            placeHolder = stringResource(R.string.sell_price),
                            isEnabled = isEditMode,
                            visualTransformation = ThousandAndSuggestionVisualTransformation(false),
                            keyboardType = KeyboardType.Number,
                            onValueChange = {
                                isError = false
                                sellPrice = it.toLongOrNull() ?: 0L
                            }
                        )
                        if (isError && price.isEmpty()) { ErrorText() }
                    }
                }

                product?.let {
                    item {

                        val variants =
                            productViewModel.getProductVariantOptions(it.product.id)
                                .collectAsState(initial = null)

                        VariantSelected(
                            variants = variants.value,
                            onVariantClicked = onVariantClicked
                        )
                    }
                }
            }
        }

        fun checkData() {
            if (
                name.isNotEmpty() && desc.isNotEmpty() && sellPrice != 0L && selectedCategory != null
            ) {
                selectedCategory?.let { category ->
                    if (product != null) {
                        onSubmitEditProduct(
                            product.product.copy(
                                name = name,
                                desc = desc,
                                sellPrice = sellPrice,
                                category = category.id
                            )
                        )
                    } else {
                        onSubmitEditProduct(
                            Product.createNewProduct(
                                name = name,
                                desc = desc,
                                sellPrice = sellPrice,
                                category = category.id
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
private fun VariantSelected(
    variants: List<VariantWithOptions>?,
    onVariantClicked: () -> Unit
) {
    Spacer(modifier = Modifier.padding(bottom = 4.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .clickable {
                onVariantClicked()
            }
            .padding(16.dp)
    ) {
        if (!variants.isNullOrEmpty()) {
            variants.forEach {
                VariantItem(it)
            }
        } else {
            SelectVariantButton()
        }
    }
}

@Composable
private fun SelectVariantButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = stringResource(id = R.string.select_variant),
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

@Composable
private fun VariantItem(
    variant: VariantWithOptions
) {
    Text(
        text = variant.variant.name,
        style = MaterialTheme.typography.body2.copy(
            fontWeight = FontWeight.Bold
        )
    )
    Text(
        text = variant.optionsString(),
        style = MaterialTheme.typography.body2
    )
    Spacer(modifier = Modifier.height(4.dp))
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
