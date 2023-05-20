package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import kotlinx.coroutines.flow.Flow

interface ReservesCategoryRepository {

    fun getReservesCategories(query: SimpleSQLiteQuery): Flow<List<ReservesCategory>>

    suspend fun insertReservesCategory(data: ReservesCategory)

    suspend fun updateReservesCategory(data: ReservesCategory)
}