package com.example.myapplication.data.source.repository

import com.example.myapplication.data.source.local.entity.room.bridge.VariantMix
import com.example.myapplication.data.source.local.entity.room.helper.VariantWithVariantMix
import kotlinx.coroutines.flow.Flow

interface VariantMixesRepository {

    fun getVariantMixProductById(idVariant: Long, idProduct: Long): Flow<VariantMix?>
    fun getVariantMixProduct(idVariant: Long): Flow<VariantWithVariantMix>
    suspend fun insertVariantMix(data: VariantMix)
    suspend fun removeVariantMix(data: VariantMix)
}
