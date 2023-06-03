package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.helper.ReservesWithCategory
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import kotlinx.coroutines.flow.Flow

interface ReservesRepository {

    fun getReservesWithCategories(reservesCategory: Long): Flow<List<ReservesWithCategory>>
    fun getReservesWithCategory(reservesCategory: Long): Flow<ReservesWithCategory?>
    fun getAllReservesWithReservesCategories(): Flow<List<ReservesWithCategory>>
    fun getReserves(query: SimpleSQLiteQuery): Flow<List<Reserves>>
    fun getReservesById(reservesId: Long): Flow<Reserves>

    suspend fun insertReserves(data: Reserves): Long

    suspend fun updateReserves(data: Reserves)

}