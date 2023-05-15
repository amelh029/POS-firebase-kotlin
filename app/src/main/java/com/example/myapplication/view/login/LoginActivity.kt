package com.example.myapplication.view.login

import android.os.Bundle
import android.content.Intent
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.compose.LoadingView
import com.example.myapplication.compose.rememberLoadingState
import com.example.myapplication.view.order_customer.OrderCustomerActivity
import com.example.myapplication.view.ui.theme.POSTheme

import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            auth = FirebaseAuth.getInstance()

            val loadingState = rememberLoadingState()

            POSTheme {

                LoadingView(
                    state = loadingState
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = LoginDestinations.LOGIN,
                    ) {
                        composable(
                            route = LoginDestinations.LOGIN
                        ) {
                            var isError by remember { mutableStateOf(false) }
                            LoginScreen(
                                isError = isError,
                                onLogin = { email, password ->
                                    loadingState.loading = true
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isError = false
                                                toMain()
                                            } else {
                                                isError = true
                                            }
                                            loadingState.loading = false
                                        }
                                },
                                onRegister = {
                                    navController.navigate(LoginDestinations.REGISTER)
                                }
                            )
                        }
                        composable(
                            route = LoginDestinations.REGISTER
                        ) {
                            RegisterScreen(
                                onBackClick = {
                                    navController.navigateUp()
                                },
                                onRegister = { email, password, store ->
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener {
                                            toMain()

                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    private fun toMain() {
        val intent = Intent(this, OrderCustomerActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}