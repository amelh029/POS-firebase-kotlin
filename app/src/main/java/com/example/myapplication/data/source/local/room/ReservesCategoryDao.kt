package com.example.myapplication.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservesCategoryDao {

    @RawQuery(observedEntities = [ReservesCategory::class])
    fun getReservesCategories(query: SupportSQLiteQuery): Flow<List<ReservesCategory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReserevesCategory(data: ReservesCategory)

    @Update
    suspend fun updateReservesCategory(data: ReservesCategory)


}