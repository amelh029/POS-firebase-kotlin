package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.master.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface SuppliersDao {

    @Query("SELECT * FROM ${Supplier.DB_NAME}")
    fun getSuppliers(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuppliers(data: Supplier)

    @Update
    fun updateSupplier(data: Supplier)
}
