package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.bridge.VariantMix
import com.example.myapplication.data.source.local.entity.room.helper.VariantWithVariantMix
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.Variant
import kotlinx.coroutines.flow.Flow


@Dao
interface VariantMixesDao {

    @Transaction
    @Query("SELECT * FROM ${Variant.DB_NAME} WHERE ${Variant.ID} = :idVariant")
    fun getVariantMixProduct(idVariant: Long): Flow<VariantWithVariantMix>

    @Transaction
    @Query("SELECT * FROM ${VariantMix.DB_NAME} WHERE ${Variant.ID} = :idVariant AND ${Product.ID} = :idProduct")
    fun getVariantMixProductById(idVariant: Long, idProduct: Long): Flow<VariantMix?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVariantMix(data: VariantMix): Long

    @Query("DELETE FROM ${VariantMix.DB_NAME} WHERE ${VariantMix.ID} = :idVariantMix")
    fun removeVariantMix(idVariantMix: Long)
}