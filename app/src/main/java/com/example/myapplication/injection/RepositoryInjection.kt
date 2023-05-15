package com.example.myapplication.injection

import android.content.Context
import com.example.myapplication.data.source.local.room.AppDatabase.Companion.getInstance
import com.example.myapplication.data.source.repository.*
import com.example.myapplication.data.source.repository.implimentation.*
import com.example.myapplication.utils.database.AppExecutors


object RepositoryInjection {

    fun providePOSRepository(context: Context): POSRepository {
        val database = getInstance(context)
        val appExecutors = AppExecutors(context)
        return POSRepository.getInstance(appExecutors, database.posDao())
    }

    fun providePaymentsRepository(context: Context): PaymentsRepository {
        val database = getInstance(context)
        return PaymentsRepositoryImpl.getInstance(database.paymentsDao())
    }

    fun provideSupplierRepository(context: Context): SuppliersRepository {
        val database = getInstance(context)
        return SuppliersRepositoryImpl.getInstance(database.suppliersDao())
    }

    fun provideCustomersRepository(context: Context): CustomersRepository {
        val database = getInstance(context)
        return CustomersRepositoryImpl.getInstance(database.customersDao())
    }

    fun provideVariantsRepository(context: Context): VariantsRepository {
        val database = getInstance(context)
        return VariantsRepositoryImpl.getInstance(database.variantsDao())
    }

    fun provideVariantOptionsRepository(context: Context): VariantOptionsRepository {
        val database = getInstance(context)
        return VariantOptionsRepositoryImpl.getInstance(database.variantOptionsDao())
    }

    fun provideCategoriesRepository(context: Context): CategoriesRepository {
        val database = getInstance(context)
        return CategoriesRepositoryImpl.getInstance(database.categoriesDao())
    }

    fun provideOutcomesRepository(context: Context): OutcomesRepository {
        val database = getInstance(context)
        return OutcomesRepositoryImpl.getInstance(
            database.outcomesDao(),
            SettingRepositoryImpl.getDataStoreInstance(context)
        )
    }

    fun provideProductsRepository(context: Context): ProductsRepository {
        val database = getInstance(context)
        return ProductsRepositoryImpl.getInstance(database.productsDao())
    }

    fun provideProductVariantsRepository(context: Context): ProductVariantsRepository {
        val database = getInstance(context)
        return ProductVariantsRepositoryImpl.getInstance(database.productVariantsDao())
    }

    fun provideOrdersRepository(context: Context): OrdersRepository {
        val database = getInstance(context)
        return OrdersRepositoryImpl.getInstance(
            database.ordersDao(),
            SettingRepositoryImpl.getDataStoreInstance(context)
        )
    }

    fun provideVariantMixesRepository(context: Context): VariantMixesRepository {
        val database = getInstance(context)
        return VariantMixesRepositoryImpl.getInstance(database.variantMixesDao())
    }

    fun provideStoreRepository(context: Context): StoreRepository {
        val database = getInstance(context)
        return StoreRepositoryImpl.getInstance(database.storeDao())
    }

    fun provideSettingRepository(context: Context): SettingRepository {
        return SettingRepositoryImpl.getDataStoreInstance(context)
    }

    fun providePromosRepository(context: Context): PromosRepository {
        val database = getInstance(context)
        return PromosRepositoryImpl.getInstance(dao = database.promoDao())
    }
    fun provideReservesRepository(context: Context): ReservesRepository{
        val database = getInstance(context)
        return ReserveRepositoryImpl.getInstance(dao = database.reservesDao())
    }
}
