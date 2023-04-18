package com.example.myapplication.data.source.repository

import kotlinx.coroutines.flow.Flow

interface SuppliersRepository {

    fun getSuppliers(): Flow<List<Supplier>>
    suspend fun insertSupplier(data: Supplier)
    suspend fun updateSupplier(data: Supplier)
}
