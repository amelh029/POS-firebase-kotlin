package com.example.myapplication.data.source.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Category
import kotlinx.coroutines.flow.Flow


interface CategoriesRepository {

    fun getCategories(query: SimpleSQLiteQuery): Flow<List<Category>>
    suspend fun insertCategory(data: Category)
    suspend fun updateCategory(data: Category)
}
