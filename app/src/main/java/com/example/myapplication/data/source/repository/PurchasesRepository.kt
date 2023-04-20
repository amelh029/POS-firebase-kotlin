package com.example.myapplication.data.source.repository

import com.example.myapplication.data.source.local.entity.helper.PurchaseProductWithProduct
import com.example.myapplication.data.source.local.entity.helper.PurchaseWithProduct
import com.example.myapplication.data.source.local.entity.room.helper.PurchaseWithSupplier
import kotlinx.coroutines.flow.Flow

interface PurchasesRepository {

    fun getPurchases(): Flow<List<PurchaseWithSupplier>>
    fun getPurchaseProducts(purchaseNo: String): Flow<List<PurchaseProductWithProduct>>
    suspend fun newPurchase(data: PurchaseWithProduct)
}
