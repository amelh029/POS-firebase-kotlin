package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

interface PromosRepository {
    suspend fun insertPromo(data: Promo)
    suspend fun updatePromo(data: Promo)
    fun getPromos(query: SupportSQLiteQuery): Flow<List<Promo>>
}
