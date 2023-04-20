package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.helper.PurchaseProductWithProduct
import com.example.myapplication.data.source.local.entity.room.helper.PurchaseWithSupplier
import com.example.myapplication.data.source.local.entity.room.master.Purchase
import com.example.myapplication.data.source.local.entity.room.master.PurchaseProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchasesDao {

    @Transaction
    @Query("SELECT * FROM ${Purchase.DB_NAME}")
    fun getPurchases(): Flow<List<PurchaseWithSupplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPurchase(data: Purchase)

    @Update
    fun updatePurchase(data: Purchase)

    @Transaction
    @Query("SELECT * FROM ${PurchaseProduct.DB_NAME} WHERE ${Purchase.NO} = :purchaseNo")
    fun getPurchasesProduct(purchaseNo: String): Flow<List<PurchaseProductWithProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPurchaseProducts(data: List<PurchaseProduct>)

    @Update
    fun updatePurchaseProduct(data: PurchaseProduct)
}
