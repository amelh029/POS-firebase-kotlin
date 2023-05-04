package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    suspend fun insertPayment(data: Payment)
    suspend fun updatePayment(data: Payment)
    fun getPayments(query: SupportSQLiteQuery) : Flow<List<Payment>>
}
