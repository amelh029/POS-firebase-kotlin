package com.example.myapplication.data.source.local.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {

    @RawQuery(observedEntities = [Category::class])
    fun getCategories(query: SupportSQLiteQuery): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(data: Category)

    @Update
    suspend fun updateCategory(data: Category)
}
