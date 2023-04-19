package com.example.myapplication.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BasicRadioButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: String,
    onClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable {
                onClicked()
            }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClicked
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = text,
            style = MaterialTheme.typography.body2
        )
    }
}
