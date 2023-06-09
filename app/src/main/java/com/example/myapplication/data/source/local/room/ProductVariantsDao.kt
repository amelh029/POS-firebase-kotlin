package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.bridge.VariantProduct
import com.example.myapplication.data.source.local.entity.room.helper.VariantProductWithOption
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductVariantsDao {

    @Transaction
    @Query("SELECT * FROM ${VariantProduct.DB_NAME} WHERE ${Product.ID} = :idProduct ORDER BY ${Variant.ID}")
    fun getVariantProducts(idProduct: Long): Flow<List<VariantProductWithOption>?>

    @Query("SELECT * FROM ${VariantProduct.DB_NAME} WHERE ${Product.ID} = :idProduct AND ${VariantOption.ID} = :idVariantOption")
    fun getVariantProduct(idProduct: Long, idVariantOption: Long): Flow<VariantProduct?>

    @Query("SELECT * FROM ${VariantProduct.DB_NAME} WHERE ${Product.ID} = :idProduct")
    fun getProductVariantsById(idProduct: Long): Flow<List<VariantProduct>>

    @Query("SELECT * FROM ${VariantProduct.DB_NAME} WHERE ${Product.ID} = :idProduct")
    fun getProductVariants(idProduct: Long): List<VariantProduct>?

    @Query("SELECT * FROM ${VariantProduct.DB_NAME} WHERE ${Product.ID} = :idProduct")
    fun getVariantProductById(idProduct: Long): Flow<VariantProduct?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVariantProduct(data: VariantProduct): Long

    @Query("DELETE FROM ${VariantProduct.DB_NAME} WHERE ${VariantOption.ID} = :idVariant AND ${Product.ID} = :idProduct")
    fun removeVariantProduct(idVariant: Long, idProduct: Long)
}
