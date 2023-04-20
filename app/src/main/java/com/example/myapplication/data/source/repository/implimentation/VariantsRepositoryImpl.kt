package com.example.myapplication.data.source.repository.implimentation

import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.data.source.local.room.VariantsDao
import com.example.myapplication.data.source.repository.VariantsRepository

class VariantsRepositoryImpl (
    private val dao: VariantsDao
) : VariantsRepository {

    companion object {
        @Volatile
        private var INSTANCE: VariantsRepositoryImpl? = null

        fun getInstance(
            dao: VariantsDao
        ): VariantsRepositoryImpl {
            if (INSTANCE == null) {
                synchronized(VariantsRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = VariantsRepositoryImpl(dao = dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getVariants() = dao.getVariants()

    override suspend fun insertVariant(data: Variant) {
        dao.insertVariant(data)
    }

    override suspend fun updateVariant(data: Variant) {
        dao.updateVariant(data)
    }
}
