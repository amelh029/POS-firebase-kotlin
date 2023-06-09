package com.example.myapplication.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PrimaryButtonView(
    modifier: Modifier = Modifier,
    buttonText: String,
    isEnabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colors.primary,
    textColor: Color = MaterialTheme.colors.onPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .run {
                return@run if (isEnabled) {
                    clickable { onClick() }
                } else { this }
            }
            .background(
                color = if (isEnabled) backgroundColor else backgroundColor.copy(alpha = ContentAlpha.disabled),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = buttonText,
            style = MaterialTheme.typography.button,
            color = textColor
        )
    }
}
