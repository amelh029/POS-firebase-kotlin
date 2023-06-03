package com.example.myapplication.view.store

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker

import com.example.myapplication.ActivityMessage
import com.example.myapplication.OpeningActivity
import com.example.myapplication.R
import com.example.myapplication.view.store.variants.VariantView
import com.example.myapplication.utils.config.DateUtils
import com.example.myapplication.view.order_customer.OrderCustomerActivity
import com.example.myapplication.view.orders.OrdersActivity
import com.example.myapplication.view.outcomes.OutcomesActivity
import com.example.myapplication.view.settings.SettingsActivity
import com.example.myapplication.view.store.Reserves.ReservesDetailMaster
import com.example.myapplication.view.store.Reserves.ReservesMaster
import com.example.myapplication.view.store.category.CategoryMasterView
import com.example.myapplication.view.store.payments.PaymentMasterView
import com.example.myapplication.view.store.product.ProductDetailMaster
import com.example.myapplication.view.store.product.ProductsMaster
import com.example.myapplication.view.store.promo.PromoMasterView
import com.example.myapplication.view.store.recap.RecapMainView
import com.example.myapplication.view.store.reservescategory.ReservesCategoryMasterView
import com.example.myapplication.view.store.stores.StoresView
import com.example.myapplication.view.ui.GeneralMenus
import com.example.myapplication.view.ui.MasterMenus
import com.example.myapplication.view.ui.theme.POSTheme
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel
import com.example.myapplication.view.viewModel.ProductViewModel
import com.example.myapplication.view.viewModel.ReservesViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class StoreActivity : ActivityMessage() {

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var reservesViewModel: ReservesViewModel

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderViewModel = OrderViewModel.getOrderViewModel(this)
        mainViewModel = MainViewModel.getMainViewModel(this)
        productViewModel = ProductViewModel.getMainViewModel(this)
        reservesViewModel = ReservesViewModel.getMainViewModel(this)

        val date = DateUtils.currentDate

        setContent {
            POSTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = StoreDestinations.MAIN_STORE
                ) {
                    composable(StoreDestinations.MAIN_STORE) {
                        MainStoreMenu(
                            orderViewModel = orderViewModel,
                            currentDate = date,
                            onGeneralMenuClicked = {
                                when (it) {
                                    GeneralMenus.NEW_ORDER -> goToOrderCustomerActivity()
                                    GeneralMenus.ORDERS -> goToOrdersActivity()
                                    GeneralMenus.SETTING -> goToSettingsActivity()
                                    else -> {
                                        // Do nothing
                                    }
                                }
                            },
                            onMasterMenuClicked = {
                                when (it) {
                                    MasterMenus.PRODUCT -> {
                                        navController.navigate(StoreDestinations.MASTER_PRODUCT)
                                    }

                                    MasterMenus.CATEGORY -> {
                                        navController.navigate(StoreDestinations.MASTER_CATEGORY)
                                    }

                                    MasterMenus.VARIANT -> {
                                        navController.navigate(StoreDestinations.MASTER_VARIANTS)
                                    }

                                    MasterMenus.RESERVES -> {
                                        navController.navigate(StoreDestinations.MASTER_RESERVES)
                                    }
                                    MasterMenus.RESERVES_CATEGORY ->{
                                        navController.navigate(StoreDestinations.MASTER_RESERVES_CATEGORY)
                                    }
                                }
                            },
                            onStoreMenuClicked = {
                                when (it) {
                                    com.example.myapplication.view.ui.StoreMenus.SALES_RECAP -> {
                                        navController.navigate(StoreDestinations.MASTER_RECAP)
                                    }

                                    com.example.myapplication.view.ui.StoreMenus.OUTCOMES -> {
                                        OutcomesActivity.createInstanceForRecap(this@StoreActivity)
                                    }

                                    com.example.myapplication.view.ui.StoreMenus.PAYMENT -> {
                                        navController.navigate(StoreDestinations.MASTER_PAYMENT)
                                    }

                                    com.example.myapplication.view.ui.StoreMenus.STORE -> {
                                        navController.navigate(StoreDestinations.MASTER_STORES)
                                    }

                                    com.example.myapplication.view.ui.StoreMenus.PROMO -> {
                                        navController.navigate(StoreDestinations.MASTER_PROMO)
                                    }

                                    com.example.myapplication.view.ui.StoreMenus.LOGOUT -> {
                                        logout()
                                    }

                                    else -> {
                                        // Do nothing
                                    }
                                }
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_PAYMENT) {
                        PaymentMasterView(
                            mainViewModel = mainViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_PROMO) {
                        PromoMasterView(
                            mainViewModel = mainViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_CATEGORY) {
                        CategoryMasterView(
                            productViewModel = productViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_RESERVES_CATEGORY){
                        ReservesCategoryMasterView(
                            reservesViewModel = reservesViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_STORES) {
                        StoresView(
                            mainViewModel = mainViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_RESERVES) {
                        ReservesMaster(
                            reserveViewModel = reservesViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            },
                            onItemClicked = {
                                navController.navigate(StoreDestinations.reservesDetail(it.id))
                            },
                            onAddReservesClicked = {
                                navController.navigate(StoreDestinations.newReserves())
                            }
                        )
                    }
                    composable(StoreDestinations.MASTER_RECAP) {
                        RecapMainView(
                            mainViewModel = mainViewModel,
                            orderViewModel = orderViewModel,
                            datePicker = buildRecapDatePicker(),
                            fragmentManager = supportFragmentManager,
                            onBackClicked = {
                                navController.popBackStack()
                            },
                            onOrdersClicked = {
                                OrdersActivity.createInstanceForRecap(
                                    context = this@StoreActivity,
                                    parameters = it
                                )
                            },
                            onOutcomesClicked = {
                                OutcomesActivity.createInstanceForRecap(
                                    context = this@StoreActivity,
                                    parameters = it
                                )
                            }
                        )
                    }

                    composable(StoreDestinations.MASTER_PRODUCT) {
                        ProductsMaster(
                            productViewModel = productViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            },
                            onItemClicked = {
                                navController.navigate(StoreDestinations.productDetail(it.id))
                            },
                            onVariantClicked = {
                                navController.navigate(StoreDestinations.productVariants(it.id))
                            },
                            onAddProductClicked = {
                                navController.navigate(StoreDestinations.newProduct())
                            }
                        )
                    }


                    val productIdArgument = navArgument(name = StoreDestinations.PRODUCT_ID) {
                        type = NavType.LongType
                    }
                    composable(
                        route = StoreDestinations.DETAIL_PRODUCT,
                        arguments = listOf(productIdArgument)
                    ) {
                        var currentId by rememberSaveable {
                            mutableStateOf(it.arguments?.getLong(StoreDestinations.PRODUCT_ID))
                        }
                        currentId?.let { id ->
                            ProductDetailMaster(
                                productViewModel = productViewModel,
                                productId = id,
                                onVariantClicked = {
                                    navController.navigate(StoreDestinations.productVariants(id))
                                },
                                onBackClicked = {
                                    navController.popBackStack()
                                },
                                onCreateNewProduct = { product ->
                                    lifecycleScope.launch {
                                        val newId = productViewModel.insertProduct(product)
                                        currentId = newId
                                    }
                                }
                            )
                        }
                    }
                    val reservesIdArgument = navArgument(name = StoreDestinations.RESERVES_ID){
                        type = NavType.LongType
                    }
                    composable(
                        route = StoreDestinations.DETAIL_RESERVES,
                        arguments = listOf(reservesIdArgument)
                    ){
                        var currentresId by rememberSaveable{
                            mutableStateOf(it.arguments?.getLong(StoreDestinations.RESERVES_ID))
                        }
                        currentresId?.let{ id ->
                            ReservesDetailMaster(
                                reservesViewModel = reservesViewModel,
                                reservesId = id,
                                onBackClicked = {
                                                navController.popBackStack()
                                                },
                                onCreateNewReserves = {reserves ->
                                    lifecycleScope.launch {
                                        val newresId = reservesViewModel.insertReserves(reserves)
                                        currentresId = newresId
                                    }
                                }
                            )
                        }

                    }

                    composable(
                        route = StoreDestinations.PRODUCT_VARIANTS,
                        arguments = listOf(productIdArgument)
                    ) {
                        it.arguments?.getLong(StoreDestinations.PRODUCT_ID)?.let { id ->
                            VariantView(
                                productViewModel = productViewModel,
                                idProduct = id,
                                onBackClicked = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                    composable(StoreDestinations.MASTER_VARIANTS) {
                        VariantView(
                            productViewModel = productViewModel,
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun logout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        goToOpening()
    }

    private fun goToOpening() {
        val intent = Intent(this, OpeningActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun buildRecapDatePicker() = MaterialDatePicker.Builder.dateRangePicker()
        .setCalendarConstraints(dateConstraint)
        .setTitleText(R.string.select_date)
        .setPositiveButtonText(R.string.select)
        .build()

    private val dateConstraint = CalendarConstraints.Builder()
        .setValidator(DateValidatorPointBackward.now())
        .build()

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

    private fun goToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}
