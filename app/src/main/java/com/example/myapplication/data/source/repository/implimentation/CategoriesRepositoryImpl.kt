package com.example.myapplication.data.source.repository.implimentation

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.room.CategoriesDao
import com.example.myapplication.data.source.repository.CategoriesRepository

class CategoriesRepositoryImpl (
    private val dao: CategoriesDao
) : CategoriesRepository {

    companion object {
        @Volatile
        private var INSTANCE: CategoriesRepositoryImpl? = null

        fun getInstance(
            dao: CategoriesDao
        ): CategoriesRepositoryImpl {
            if (INSTANCE == null) {
                synchronized(CategoriesRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = CategoriesRepositoryImpl(dao = dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getCategories(query: SimpleSQLiteQuery) = dao.getCategories(query)

    override suspend fun insertCategory(data: Category) {
        dao.insertCategory(data)
    }

    override suspend fun updateCategory(data: Category) {
        dao.updateCategory(data)
    }
}
