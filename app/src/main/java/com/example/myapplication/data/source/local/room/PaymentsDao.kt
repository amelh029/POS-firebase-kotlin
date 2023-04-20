package com.example.myapplication.data.source.local.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentsDao {

    @RawQuery(observedEntities = [Payment::class])
    fun getPayments(query: SupportSQLiteQuery): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPayment(data: Payment)

    @Update
    fun updatePayment(data: Payment)
}
