package com.example.myapplication.data.source.repository

import com.example.myapplication.data.source.local.entity.room.bridge.VariantProduct
import kotlinx.coroutines.flow.Flow

interface ProductVariantsRepository {

    fun getVariantProduct(
        idProduct: Long,
        idVariantOption: Long
    ): Flow<VariantProduct?>
    suspend fun isProductHasVariants(idProduct: Long): Boolean
    fun getVariantsProductById(idProduct: Long): Flow<List<VariantProduct>>
    fun getVariantProductById(idProduct: Long): Flow<VariantProduct?>
    suspend fun insertVariantProduct(data: VariantProduct)
    suspend fun removeVariantProduct(data: VariantProduct)
}
