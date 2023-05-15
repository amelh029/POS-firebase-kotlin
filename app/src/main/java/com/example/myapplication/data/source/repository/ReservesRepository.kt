package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import kotlinx.coroutines.flow.Flow

interface ReservesRepository {


    fun getReserves(query: SimpleSQLiteQuery): Flow<List<Reserves>>
    fun getReservesById(reservesId: Long): Flow<Reserves>

    fun insertReserves(data: Reserves)

    suspend fun updateReserves(data: Reserves)

}