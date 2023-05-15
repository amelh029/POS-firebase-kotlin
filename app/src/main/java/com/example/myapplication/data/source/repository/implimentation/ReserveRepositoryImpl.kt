package com.example.myapplication.data.source.repository.implimentation

import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.room.ReserveDao
import com.example.myapplication.data.source.repository.ReservesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

    override fun getReserves(query: SimpleSQLiteQuery) = dao.getReserves(query)
    override fun getReservesById(reservesId: Long) = dao.getReservesAsFlow(reservesId)
    override fun insertReserves(data: Reserves) = dao.insetReserves(data)


    override suspend fun updateReserves(data: Reserves){
        dao.updateReserves(data)
    }

}