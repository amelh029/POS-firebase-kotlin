package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.master.Outcome
import kotlinx.coroutines.flow.Flow

@Dao
interface OutcomesDao {

    @Query("SELECT * FROM ${Outcome.DB_NAME} WHERE date(${Outcome.DATE}) = date(:date)")
    fun getOutcome(date: String): Flow<List<Outcome>>

    @Query("SELECT * FROM ${Outcome.DB_NAME} WHERE ${Outcome.STORE} = :store AND date(${Outcome.DATE}) BETWEEN date(:from) AND date(:until)")
    fun getOutcome(from: String, until: String, store: Long): Flow<List<Outcome>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOutcome(data: Outcome): Long

    @Update
    fun updateOutcome(data: Outcome)
}
