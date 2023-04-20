package com.example.myapplication.view.order_customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myapplication.R
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.utils.config.toIDR
import com.example.myapplication.view.viewModel.ProductViewModel


@Composable
fun ProductCustomerItemView(
    productViewModel: ProductViewModel,
    product: Product,
    currentAmount: Int?,
    onItemClick: (isAdd: Boolean, hasVariant: Boolean) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.surface)
            .padding(8.dp),
    ) {
        val (title, subTitle, price, buttons) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(title) {
                    linkTo(
                        start = parent.start,
                        startMargin = 16.dp,
                        end = parent.end,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(parent.top)
                },
            text = product.name,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier
                .constrainAs(subTitle) {
                    linkTo(
                        start = parent.start,
                        startMargin = 16.dp,
                        end = parent.end,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(title.bottom)
                },
            text = product.desc
        )
        Text(
            modifier = Modifier
                .constrainAs(price) {
                    start.linkTo(parent.start, margin = 16.dp)
                    linkTo(top = subTitle.bottom, bottom = parent.bottom)
                },
            text = product.sellPrice.toIDR(),
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Row(
            modifier = Modifier
                .constrainAs(buttons) {
                    end.linkTo(parent.end)
                    linkTo(top = subTitle.bottom, bottom = parent.bottom)
                },
        ) {

            if (product.isActive) {
                var hasVariant by remember {
                    mutableStateOf<Boolean?>(null)
                }

                LaunchedEffect(key1 = true) {
                    hasVariant = productViewModel.isProductHasVariant(product.id)
                }

                hasVariant?.let {
                    if (it) {
                        SelectVariantButton(onItemClick = {
                            onItemClick(true, true)
                        })
                    } else {
                        AmountOption(currentAmount = currentAmount, onItemClick = { isAdd ->
                            onItemClick(isAdd, false)
                        })
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.finished),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Red
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RowScope.SelectVariantButton(
    onItemClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .clickable {
                onItemClick()
            }
            .align(Alignment.CenterVertically),
        text = stringResource(id = R.string.select_variant),
        style = MaterialTheme.typography.body1
    )
    Spacer(modifier = Modifier.width(8.dp))
    Icon(
        painter = painterResource(id = R.drawable.ic_arrow_right),
        tint = MaterialTheme.colors.onSurface,
        contentDescription = null
    )
}

@Composable
private fun RowScope.AmountOption(
    currentAmount: Int?,
    onItemClick: (isAdd: Boolean) -> Unit
) {
    currentAmount?.let {
        IconButton(
            onClick = {
                onItemClick(false)
            }
        ) {
            Image(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = R.drawable.ic_remove_circle),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = currentAmount.toString(),
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
    }
    IconButton(
        onClick = {
            onItemClick(true)
        }
    ) {
        Image(
            modifier = Modifier.size(25.dp),
            painter = painterResource(id = R.drawable.ic_add_circle),
            contentDescription = null
        )
    }
}