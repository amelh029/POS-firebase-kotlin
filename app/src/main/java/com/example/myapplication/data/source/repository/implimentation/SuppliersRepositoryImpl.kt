package com.example.myapplication.data.source.repository.implimentation

import com.example.myapplication.data.source.local.entity.room.master.Supplier
import com.example.myapplication.data.source.local.room.SuppliersDao
import com.example.myapplication.data.source.repository.SuppliersRepository
import kotlinx.coroutines.flow.Flow

class SuppliersRepositoryImpl(
    private val dao: SuppliersDao
) : SuppliersRepository {

    companion object {
        @Volatile
        private var INSTANCE: SuppliersRepositoryImpl? = null

        fun getInstance(
            dao: SuppliersDao
        ): SuppliersRepositoryImpl {
            if (INSTANCE == null) {
                synchronized(SuppliersRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = SuppliersRepositoryImpl(dao = dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getSuppliers(): Flow<List<Supplier>> = dao.getSuppliers()

    override suspend fun insertSupplier(data: Supplier) {
        dao.insertSuppliers(data)
    }

    override suspend fun updateSupplier(data: Supplier) {
        dao.updateSupplier(data)
    }
}
