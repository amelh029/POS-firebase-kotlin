package com.example.myapplication.injection

import android.content.Context
import com.example.myapplication.data.source.domain.*
import com.example.myapplication.data.source.domain.implementation.*
import com.example.myapplication.data.source.local.room.AppDatabase.Companion.getInstance
import com.example.myapplication.data.source.preference.OrderPref
import com.example.myapplication.data.source.repository.implimentation.SettingRepositoryImpl


object DomainInjection {
    fun provideGetProductVariantOptions(context: Context): GetProductVariantOptions {
        val database = getInstance(context)
        return GetProductVariantOptionsImpl(database.productVariantsDao())
    }

    fun provideNewOrder(context: Context): NewOrder {
        val database = getInstance(context)
        return NewOrderImpl(
            dao = database.ordersDao(),
//            productDao = database.productsDao(),
            posDao = database.posDao(),
            orderPref = OrderPref(context),
            settingRepository = SettingRepositoryImpl.getDataStoreInstance(context)
        )
    }

    fun provideGetProductOrder(context: Context): GetProductOrder {
        val database = getInstance(context)
        return GetProductOrderImpl(
            dao = database.ordersDao(),
            productsDao = database.productsDao()
        )
    }

    fun provideGetIncomesRecapData(context: Context): GetRecapData {
        return GetRecapDataImpl(
            RepositoryInjection.provideOrdersRepository(context),
            RepositoryInjection.provideOutcomesRepository(context),
            provideGetProductOrder(context)
        )
    }

    fun providePayOrder(context: Context): PayOrder {
        return PayOrderImpl(
            RepositoryInjection.provideOrdersRepository(context)
        )
    }

    fun provideGetOrdersGeneralMenuBadge(context: Context): GetOrdersGeneralMenuBadge {
        return GetOrdersGeneralMenuBadgeImpl(
            RepositoryInjection.provideOrdersRepository(context)
        )
    }

    fun provideNewOutcome(context: Context): NewOutcome {
        return NewOutcomeImpl(
            RepositoryInjection.provideSettingRepository(context),
            RepositoryInjection.provideOutcomesRepository(context)
        )
    }

    fun provideUpdateOrderProducts(context: Context): UpdateOrderProducts {
        val database = getInstance(context)
        return UpdateOrderProductsImpl(
            database.ordersDao(),
            database.posDao()
        )
    }
}
