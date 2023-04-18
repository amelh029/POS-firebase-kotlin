package com.example.myapplication.data.source.repository

import kotlinx.coroutines.flow.Flow

interface StoreRepository {

    fun getStores(): Flow<List<Store>>
    suspend fun getStore(id: Long): Store?
    suspend fun insertStore(store: Store)
    suspend fun updateStore(store: Store)
}
