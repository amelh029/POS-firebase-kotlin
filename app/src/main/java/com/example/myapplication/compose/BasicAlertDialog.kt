package com.example.myapplication.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun BasicAlertDialog(
    titleText: String,
    descText: String,
    positiveAction: () -> Unit,
    positiveText: String,
    negativeAction: (() -> Unit)? = null,
    negativeText: String? = null,
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = titleText,
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = descText,
                    style = MaterialTheme.typography.body2.copy(
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    PrimaryButtonView(
                        modifier = Modifier
                            .weight(1f),
                        buttonText = positiveText,
                        onClick = positiveAction
                    )
                    if (negativeAction != null && negativeText != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        PrimaryButtonView(
                            modifier = Modifier
                                .weight(1f),
                            buttonText = negativeText,
                            onClick = negativeAction
                        )
                    }
                }
            }
        }
    }
}
