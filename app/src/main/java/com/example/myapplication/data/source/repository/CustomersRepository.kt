package com.example.myapplication.data.source.repository

import com.example.myapplication.data.source.local.entity.room.master.Customer
import kotlinx.coroutines.flow.Flow


interface CustomersRepository {

    fun getCustomers(): Flow<List<Customer>>
    suspend fun insertCustomer(data: Customer): Long
}
