package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Promo
import kotlinx.coroutines.flow.Flow

interface PromosRepository {
    suspend fun insertPromo(data: Promo)
    suspend fun updatePromo(data: Promo)
    fun getPromos(query: SupportSQLiteQuery): Flow<List<Promo>>
}
