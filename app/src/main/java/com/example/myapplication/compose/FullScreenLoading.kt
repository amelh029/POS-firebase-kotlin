package com.example.myapplication.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp




@Composable
fun LoadingView(
    state: LoadingState,
    content: @Composable () -> Unit
) {
    content()
    if (state.loading) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.surface.copy(
                        0.2f
                    )
                )
                .fillMaxSize()
                .clickable(
                    indication = null, // disable ripple effect
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { }
                ),
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = MaterialTheme.colors.surface,
                strokeWidth = 4.dp
            )
        }
    }
}

class LoadingState(isLoading: Boolean) {
    var loading by mutableStateOf(isLoading)
}

@Composable
fun rememberLoadingState(): LoadingState =
    remember { LoadingState(false) }
