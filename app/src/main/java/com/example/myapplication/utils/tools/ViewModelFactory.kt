package com.example.myapplication.utils.tools

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel
import com.example.myapplication.view.viewModel.ProductViewModel
import com.example.myapplication.view.viewModel.UserViewModel

class ViewModelFactory private constructor(
    private val repository: SoliteRepository,
    private val paymentsRepository: PaymentsRepository,
    private val supplierRepository: SuppliersRepository,
    private val customersRepository: CustomersRepository,
    private val variantsRepository: VariantsRepository,
    private val variantOptionsRepository: VariantOptionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val outcomesRepository: OutcomesRepository,
    private val productsRepository: ProductsRepository,
    private val productVariantsRepository: ProductVariantsRepository,
    private val getProductVariantOptions: GetProductVariantOptions,
    private val ordersRepository: OrdersRepository,
    private val newOrder: NewOrder,
    private val getProductOrder: GetProductOrder,
    private val getRecapData: GetRecapData,
    private val variantMixesRepository: VariantMixesRepository,
    private val payOrder: PayOrder,
    private val getOrdersGeneralMenuBadge: GetOrdersGeneralMenuBadge,
    private val storeRepository: StoreRepository,
    private val settingRepository: SettingRepository,
    private val newOutcome: NewOutcome,
    private val updateOrderProducts: UpdateOrderProducts,
    private val promosRepository: PromosRepository,
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = ViewModelFactory(
                            repository = provideSoliteRepository(context),
                            paymentsRepository = providePaymentsRepository(context),
                            supplierRepository = provideSupplierRepository(context),
                            customersRepository = provideCustomersRepository(context),
                            variantsRepository = provideVariantsRepository(context),
                            variantOptionsRepository = provideVariantOptionsRepository(context),
                            categoriesRepository = provideCategoriesRepository(context),
                            outcomesRepository = provideOutcomesRepository(context),
                            productsRepository = provideProductsRepository(context),
                            productVariantsRepository = provideProductVariantsRepository(context),
                            getProductVariantOptions = provideGetProductVariantOptions(context),
                            ordersRepository = provideOrdersRepository(context),
                            newOrder = provideNewOrder(context),
                            getProductOrder = provideGetProductOrder(context),
                            getRecapData = provideGetIncomesRecapData(context),
                            variantMixesRepository = provideVariantMixesRepository(context),
                            payOrder = providePayOrder(context),
                            getOrdersGeneralMenuBadge = provideGetOrdersGeneralMenuBadge(context),
                            storeRepository = provideStoreRepository(context),
                            settingRepository = provideSettingRepository(context),
                            newOutcome = provideNewOutcome(context),
                            updateOrderProducts = provideUpdateOrderProducts(context),
                            promosRepository = providePromosRepository(context)
                        )
                    }
                }
            }
            return INSTANCE!!
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    paymentRepository = paymentsRepository,
                    supplierRepository = supplierRepository,
                    customersRepository = customersRepository,
                    outcomesRepository = outcomesRepository,
                    storeRepository = storeRepository,
                    settingRepository = settingRepository,
                    newOutcome = newOutcome,
                    promosRepository = promosRepository
                ) as T
            }

            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(
                    orderRepository = ordersRepository,
                    newOrder = newOrder,
                    getProductOrder = getProductOrder,
                    getRecapData = getRecapData,
                    payOrder = payOrder,
                    getOrdersGeneralMenuBadge = getOrdersGeneralMenuBadge,
                    updateOrderProducts = updateOrderProducts
                ) as T
            }

            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(
                    variantsRepository = variantsRepository,
                    variantOptionsRepository = variantOptionsRepository,
                    categoriesRepository = categoriesRepository,
                    productsRepository = productsRepository,
                    productVariantsRepository = productVariantsRepository,
                    getProductVariantOptions = getProductVariantOptions,
                    variantMixesRepository = variantMixesRepository
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
