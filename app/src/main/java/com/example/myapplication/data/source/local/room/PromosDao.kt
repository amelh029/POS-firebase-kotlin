package com.example.myapplication.data.source.local.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Promo
import kotlinx.coroutines.flow.Flow

@Dao
interface PromosDao {

    @RawQuery(observedEntities = [Promo::class])
    fun getPromos(query: SupportSQLiteQuery): Flow<List<Promo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPromo(data: Promo)

    @Update
    fun updatePromo(data: Promo)
}
