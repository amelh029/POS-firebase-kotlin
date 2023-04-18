package com.example.myapplication.view.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.BasicEditText
import com.example.myapplication.compose.BasicTopBar
import com.example.myapplication.compose.PrimaryButtonView
import com.example.myapplication.utils.config.isNotValidEmail
import com.example.myapplication.utils.config.isNotValidPassword


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegister: (
        email: String,
        password: String,
        store: String
    ) -> Unit
) {
    Scaffold(
        topBar = {
            BasicTopBar(
                titleText = stringResource(R.string.registration_form),
                elevation = 0.dp,
                onBackClicked = onBackClick
            )
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .padding(padding),
                color = MaterialTheme.colors.primary
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var confirmPassword by remember { mutableStateOf("") }
                    var store by remember { mutableStateOf("") }
                    val emailErrorMessage by remember {
                        derivedStateOf {
                            when {
                                email.isEmpty() -> R.string.field_can_not_emtpy
                                email.isNotValidEmail() -> R.string.fill_with_email_correct_format
                                else -> null
                            }
                        }
                    }
                    val passwordErrorMessage by remember {
                        derivedStateOf {
                            when {
                                password.isEmpty() -> R.string.field_can_not_emtpy
                                password.isNotValidPassword() -> R.string.password_must_more_8_character
                                else -> null
                            }
                        }
                    }
                    val confirmPasswordErrorMessage by remember {
                        derivedStateOf {
                            when {
                                confirmPassword.isEmpty() -> R.string.field_can_not_emtpy
                                confirmPassword.isNotValidPassword() -> R.string.password_must_more_8_character
                                confirmPassword != password -> R.string.confirm_password_must_match_password
                                else -> null
                            }
                        }
                    }
                    val storeErrorMessage by remember {
                        derivedStateOf {
                            when {
                                store.isEmpty() -> R.string.field_can_not_emtpy
                                else -> null
                            }
                        }
                    }
                    val isButtonEnabled by remember {
                        derivedStateOf {
                            emailErrorMessage == null && passwordErrorMessage == null && storeErrorMessage == null
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        BasicEditText(
                            value = email,
                            keyboardType = KeyboardType.Email,
                            placeHolder = stringResource(R.string.enter_email),
                            onValueChange = {
                                email = it
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        emailErrorMessage?.let {

                            Text(
                                text = stringResource(id = it),
                                color = Color.Red
                            )
                        }
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
                        Spacer(modifier = Modifier.height(4.dp))
                        passwordErrorMessage?.let {
                            Text(
                                text = stringResource(id = it),
                                color = Color.Red
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        BasicEditText(
                            value = confirmPassword,
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            placeHolder = stringResource(R.string.enter_confirm_password),
                            onValueChange = {
                                confirmPassword = it
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        confirmPasswordErrorMessage?.let {
                            Text(
                                text = stringResource(id = it),
                                color = Color.Red
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        BasicEditText(
                            value = store,
                            placeHolder = stringResource(R.string.enter_store_name),
                            onValueChange = {
                                store = it
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        storeErrorMessage?.let {
                            Text(
                                text = stringResource(id = it),
                                color = Color.Red
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    PrimaryButtonView(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.surface,
                        isEnabled = isButtonEnabled,
                        buttonText = stringResource(id = R.string.register),
                        onClick = {
                            onRegister(email, password, store)
                        }
                    )
                }
            }
        }
    )
}