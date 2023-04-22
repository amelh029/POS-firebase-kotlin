package com.example.myapplication.view.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.ExperimentalMaterialApi
import com.example.myapplication.ActivityMessage
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.view.order_customer.OrderCustomerActivity
import com.example.myapplication.view.orders.OrdersActivity
import com.example.myapplication.view.ui.GeneralMenus
import com.example.myapplication.view.ui.theme.POSTheme
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel

class SettingsActivity : ActivityMessage(){

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var mainViewModel: MainViewModel

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderViewModel = OrderViewModel.getOrderViewModel(this)
        mainViewModel = MainViewModel.getMainViewModel(this)

        val date = DateUtils.currentDate

        setContent {

            POSTheme {
                SettingsMainMenu(
                    orderViewModel = orderViewModel,
                    mainViewModel = mainViewModel,
                    currentDate = date,
                    onGeneralMenuClicked = {
                        when (it) {
                            GeneralMenus.NEW_ORDER -> goToOrderCustomerActivity()
                            GeneralMenus.ORDERS -> goToOrdersActivity()
                            GeneralMenus.STORE -> goToStoreActivity()
                            else -> {
                                // Do nothing
                            }
                        }
                    },
                    onDarkModeChange = {
                        val delegate = if (it) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }

                        AppCompatDelegate.setDefaultNightMode(delegate)
                        reLaunchSettingActivity()
                    }
                )
            }
        }
    }

    private fun goToOrderCustomerActivity() {
        val intent = Intent(this, OrderCustomerActivity::class.java)
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

    private fun goToStoreActivity() {
        val intent = Intent(this, StoreActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun reLaunchSettingActivity() {
        startActivity(intent)
        finish()
    }
}
