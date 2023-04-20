package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.helper.VariantWithOptions
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import kotlinx.coroutines.flow.Flow

interface VariantOptionsRepository {
    fun getVariantOptions(query: SupportSQLiteQuery): Flow<List<VariantOption>>
    fun getVariantsWithOptions(): Flow<List<VariantWithOptions>>
    suspend fun insertVariantOption(data: VariantOption)
    suspend fun updateVariantOption(data: VariantOption)
}
