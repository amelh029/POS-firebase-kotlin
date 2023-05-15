package com.example.myapplication.view.order_customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ActivityMessage
import com.example.myapplication.R
import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.view.orders.OrdersActivity
import com.example.myapplication.view.settings.SettingsActivity
import com.example.myapplication.view.store.StoreActivity
import com.example.myapplication.view.ui.GeneralMenus
import com.example.myapplication.view.ui.theme.POSTheme
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel
import com.example.myapplication.view.viewModel.ProductViewModel
import com.example.myapplication.view.viewModel.ReservesViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.launch


class OrderCustomerActivity : ActivityMessage() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var reserveViewModel: ReservesViewModel

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productViewModel = ProductViewModel.getMainViewModel(this)
        mainViewModel = MainViewModel.getMainViewModel(this)
        orderViewModel = OrderViewModel.getOrderViewModel(this)
        reserveViewModel = ReservesViewModel.getMainViewModel(this)

        setContent{
            POSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = OrderCustomerDestinations.SELECT_ITEMS
                    ) {
                        composable(
                            OrderCustomerDestinations.SELECT_ITEMS
                        ) {
                            OrderSelectItems(
                                productViewModel = productViewModel,
                                orderViewModel = orderViewModel,
                                onItemClick = { product, isAdd, hasVariant ->
                                    lifecycleScope.launch {
                                        if (hasVariant) {
                                            navController.navigate(
                                                OrderCustomerDestinations.selectVariants(
                                                    product.id
                                                )
                                            )
                                        } else {
                                            if (isAdd) {
                                                orderViewModel.addProductToBucket(
                                                    ProductOrderDetail.productNoVariant(product)
                                                )
                                            } else {
                                                orderViewModel.decreaseProduct(
                                                    ProductOrderDetail.productNoVariant(product)
                                                )
                                            }
                                        }
                                    }
                                },
                                onClickOrder = {
                                    navController.navigate(OrderCustomerDestinations.SELECT_CUSTOMERS)
                                },
                                onGeneralMenuClicked = {
                                    when (it) {
                                        GeneralMenus.ORDERS -> goToOrdersActivity()
                                        GeneralMenus.STORE -> goToStoreActivity()
                                        GeneralMenus.SETTING -> goToSettingsActivity()
                                        else -> {
                                            // Don nothing
                                        }
                                    }
                                }
                            )
                        }

                        val productIdArgument =
                            navArgument(name = OrderCustomerDestinations.PRODUCT_ID) {
                                type = NavType.LongType
                            }
                        composable(
                            route = OrderCustomerDestinations.SELECT_VARIANTS,
                            arguments = listOf(productIdArgument)
                        ) {
                            it.arguments?.getLong(OrderCustomerDestinations.PRODUCT_ID)?.let { id ->
                                OrderSelectVariants(
                                    productId = id,
                                    viewModel = productViewModel,
                                    onBackClicked = {
                                        navController.popBackStack()
                                    },
                                    onAddToBucketClicked = {
                                        lifecycleScope.launch {
                                            orderViewModel.addProductToBucket(it)
                                            navController.popBackStack()
                                        }
                                    }
                                )
                            }
                        }
                        composable(
                            OrderCustomerDestinations.SELECT_CUSTOMERS
                        ) {
                            ProvideWindowInsets(
                                windowInsetsAnimationsEnabled = true
                            ) {
                                OrderCustomerName(
                                    mainViewModel = mainViewModel,
                                    onBackClicked = {
                                        navController.popBackStack()
                                    },
                                    onNewOrder = { customer, isTakeAway ->
                                        orderViewModel.newOrder(
                                            customer = customer,
                                            isTakeAway = isTakeAway
                                        )
                                        goToOrdersActivity()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goToStoreActivity() {
        val intent = Intent(this, StoreActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun goToOrdersActivity() {
        val intent = Intent(this, OrdersActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun goToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}