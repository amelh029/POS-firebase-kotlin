package com.example.myapplication.data.source.repository.implimentation

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import com.example.myapplication.data.source.local.room.ReservesCategoryDao
import com.example.myapplication.data.source.repository.ReservesCategoryRepository


class ReservesCategoriesRepositoryImpl(
    private val dao: ReservesCategoryDao
) : ReservesCategoryRepository {

    companion object {
        @Volatile
        private var INSTANCE: ReservesCategoriesRepositoryImpl? = null

        fun getInstance(
            dao: ReservesCategoryDao
        ): ReservesCategoriesRepositoryImpl {
            if (INSTANCE == null) {
                synchronized(ReservesCategoriesRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = ReservesCategoriesRepositoryImpl(dao = dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getReservesCategories(query: SimpleSQLiteQuery) = dao.getReservesCategories(query)

    override suspend fun insertReservesCategory(data: ReservesCategory){
        dao.insertReserevesCategory(data)
    }

    override suspend fun updateReservesCategory(data: ReservesCategory) {
        dao.updateReservesCategory(data)
    }

}