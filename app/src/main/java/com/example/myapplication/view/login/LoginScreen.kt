package com.example.myapplication.view.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.PrimaryButtonView


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    isError: Boolean,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Image(
                modifier = Modifier
                    .weight(1f),
                painter = painterResource(id = R.drawable.colibri),
                contentDescription = null
            )
            if (isError) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(
                            color = MaterialTheme.colors.surface.copy(
                                0.3f
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = stringResource(R.string.email_or_password_not_found),
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            BasicEditText(
                value = email,
                keyboardType = KeyboardType.Email,
                placeHolder = stringResource(R.string.enter_email),
                onValueChange = {
                    email = it
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            BasicEditText(
                value = password,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                placeHolder = stringResource(R.string.enter_password),
                onValueChange = {
                    password = it
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButtonView(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                isEnabled = email.isNotEmpty() && password.isNotEmpty(),
                buttonText = stringResource(R.string.login),
                backgroundColor = MaterialTheme.colors.surface,
                onClick = {
                    onLogin(email, password)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButtonView(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                backgroundColor = MaterialTheme.colors.surface,
                buttonText = stringResource(R.string.register),
                onClick = { onRegister() }
            )
        }
    }
}
