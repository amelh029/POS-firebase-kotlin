package com.example.myapplication.utils.tools

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.source.domain.*
import com.example.myapplication.data.source.repository.*
import com.example.myapplication.injection.DomainInjection.provideGetIncomesRecapData
import com.example.myapplication.injection.DomainInjection.provideGetOrdersGeneralMenuBadge
import com.example.myapplication.injection.DomainInjection.provideGetProductOrder
import com.example.myapplication.injection.DomainInjection.provideGetProductVariantOptions
import com.example.myapplication.injection.DomainInjection.provideNewOrder
import com.example.myapplication.injection.DomainInjection.provideNewOutcome
import com.example.myapplication.injection.DomainInjection.providePayOrder
import com.example.myapplication.injection.DomainInjection.provideUpdateOrderProducts
import com.example.myapplication.injection.RepositoryInjection.provideCategoriesRepository
import com.example.myapplication.injection.RepositoryInjection.provideCustomersRepository
import com.example.myapplication.injection.RepositoryInjection.provideOrdersRepository
import com.example.myapplication.injection.RepositoryInjection.provideOutcomesRepository
import com.example.myapplication.injection.RepositoryInjection.providePOSRepository
import com.example.myapplication.injection.RepositoryInjection.providePaymentsRepository
import com.example.myapplication.injection.RepositoryInjection.provideProductVariantsRepository
import com.example.myapplication.injection.RepositoryInjection.provideProductsRepository
import com.example.myapplication.injection.RepositoryInjection.providePromosRepository
import com.example.myapplication.injection.RepositoryInjection.provideReservesRepository
import com.example.myapplication.injection.RepositoryInjection.provideSettingRepository
import com.example.myapplication.injection.RepositoryInjection.provideStoreRepository
import com.example.myapplication.injection.RepositoryInjection.provideSupplierRepository
import com.example.myapplication.injection.RepositoryInjection.provideVariantMixesRepository
import com.example.myapplication.injection.RepositoryInjection.provideVariantOptionsRepository
import com.example.myapplication.injection.RepositoryInjection.provideVariantsRepository
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel
import com.example.myapplication.view.viewModel.ProductViewModel
import com.example.myapplication.view.viewModel.ReservesViewModel
import com.example.myapplication.view.viewModel.UserViewModel

class ViewModelFactory private constructor(
    private val repository: POSRepository,
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
    private val reservesRepository: ReservesRepository
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = ViewModelFactory(
                            repository = providePOSRepository(context),
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
                            promosRepository = providePromosRepository(context),
                            reservesRepository = provideReservesRepository(context)

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
                    promosRepository = promosRepository,
                    reservesRepository = reservesRepository
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
            modelClass.isAssignableFrom(ReservesViewModel::class.java)->{
                ReservesViewModel(
                    reservesRepository = reservesRepository
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
