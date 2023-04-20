package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.master.Variant
import kotlinx.coroutines.flow.Flow

@Dao
interface VariantsDao {

    @Query("SELECT * FROM ${Variant.DB_NAME}")
    fun getVariants(): Flow<List<Variant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVariant(data: Variant): Long

    @Update
    fun updateVariant(data: Variant)
}
