package com.example.myapplication.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.view.ui.theme.POSTheme


@Composable
@ExperimentalComposeUiApi
fun BasicEditText (
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    value: String,
    placeHolder: String,
    isEnabled: Boolean = true,
    onValueChange: (String) -> Unit,
    onAction: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(8.dp)
    Surface(
        modifier = Modifier
            .heightIn(min = 50.dp)
            .fillMaxWidth(),
        color = if (isEnabled) MaterialTheme.colors.surface.copy(
            alpha = 0.3f
        ) else Color.Transparent,
        shape = shape
    ) {
        Row(
            modifier = modifier
                .run {
                    return@run if (isEnabled) {
                        border(
                            width = 0.5.dp,
                            color = MaterialTheme.colors.onSurface,
                            shape = shape
                        )
                    } else {
                        this
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                if (isEnabled) {
                    EnabledContent(
                        visualTransformation = visualTransformation,
                        keyboardType = keyboardType,
                        imeAction = imeAction,
                        value = value,
                        placeHolder = placeHolder,
                        onAction = onAction,
                        onValueChange = onValueChange
                    )
                } else {
                    DisabledContent(value = value, placeHolder = placeHolder)
                }
            }
            if (isEnabled && value.isNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onValueChange("")
                        },
                    painter = painterResource(id = R.drawable.ic_close),
                    tint = MaterialTheme.colors.onSurface,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun BoxScope.EnabledContent(
    visualTransformation: VisualTransformation,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    value: String,
    placeHolder: String,
    onAction: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        value = value,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions {
            onAction?.invoke()
        },
        onValueChange = {
            onValueChange(it)
        },
        textStyle = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.onPrimary
        ),
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(
            MaterialTheme.colors.onPrimary
        ),
    )
    if (value.isEmpty()) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterStart),
            text = placeHolder,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(
                alpha = 0.6f
            )
        )
    }
}

@Composable
private fun DisabledContent(
    value: String,
    placeHolder: String,
) {
    Column {
        Text(
            text = placeHolder,
            style = MaterialTheme.typography.overline.copy(
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = value,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
@ExperimentalComposeUiApi
@Preview(showBackground = true)
private fun PreviewEnabled() {
    POSTheme {
        BasicEditText(
            value = "Siomay Ayam",
            placeHolder = "Produk",
            onValueChange = {},
            isEnabled = true
        )
    }
}

@Composable
@ExperimentalComposeUiApi
@Preview(showBackground = true)
private fun PreviewDisabled() {
    POSTheme {
        BasicEditText(
            value = "Siomay Ayam",
            placeHolder = "Produk",
            onValueChange = {},
            isEnabled = false
        )
    }
}
