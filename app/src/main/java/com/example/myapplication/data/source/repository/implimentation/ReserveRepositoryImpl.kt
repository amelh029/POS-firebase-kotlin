package com.example.myapplication.data.source.repository.implimentation


import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.room.ReserveDao
import com.example.myapplication.data.source.repository.ReservesRepository


class ReserveRepositoryImpl(
    private val dao: ReserveDao
) : ReservesRepository {
    companion object{
        @Volatile
        private var INSTANCE: ReserveRepositoryImpl? = null

        fun getInstance(
            dao: ReserveDao
        ): ReserveRepositoryImpl{
            if(INSTANCE == null){
                synchronized(ReserveRepositoryImpl::class.java){
                    if(INSTANCE == null){
                        INSTANCE = ReserveRepositoryImpl(dao = dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getReservesWithCategories(reservesCategory: Long) = dao.getReservesWithCategories(reservesCategory)

    override fun getReservesWithCategory(reservesId: Long) = dao.getReservesWithCategory(reservesId)

    override fun getAllReservesWithReservesCategories() = dao.getAllReservesWithCategories()

    override fun getReserves(query: SimpleSQLiteQuery) = dao.getReserves(query)
    override fun getReservesById(reservesId: Long) = dao.getReservesAsFlow(reservesId)

    override suspend fun insertReserves(data: Reserves) = dao.insertReserves(data)


    override suspend fun updateReserves(data: Reserves){
        dao.updateReserves(data)
    }

}