package com.example.myapplication.view.viewModel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import com.example.myapplication.data.source.repository.ReservesCategoryRepository
import com.example.myapplication.data.source.repository.ReservesRepository
import kotlinx.coroutines.launch

class ReservesViewModel (

    private val reservesRepository: ReservesRepository,
    private val reservesCategoryRepository: ReservesCategoryRepository,


): ViewModel(){

    companion object: ViewModelFromFactory<ReservesViewModel>(){
        fun getMainViewModel(activity: FragmentActivity): ReservesViewModel{
            return buildViewModel(activity, ReservesViewModel::class.java)
        }
    }

    fun getReserves(idReserves: Long) = reservesRepository.getReservesById(idReserves)

    suspend fun insertReserves(data: Reserves) = reservesRepository.insertReserves(data)

    suspend fun updateRepository(data: Reserves){
        viewModelScope.launch {
            reservesRepository.updateReserves(data)
        }
    }
    fun getReserves(query: SimpleSQLiteQuery) = reservesRepository.getReserves(query)

    fun updateReservesCategory(data: ReservesCategory){
        viewModelScope.launch {
            reservesCategoryRepository.updateReservesCategory(data)
        }
    }
    fun getReservesCategories(query: SimpleSQLiteQuery) =
        reservesCategoryRepository.getReservesCategories(query)


    fun getReservesWithCategories(reservesCategory: Long) =
        reservesRepository.getReservesWithCategories(reservesCategory)

    fun getReservesWithCategory(idReserves: Long) =
        reservesRepository.getReservesWithCategory(idReserves)
    fun insertReservesCategory(data: ReservesCategory) {
        viewModelScope.launch {
            reservesCategoryRepository.insertReservesCategory(data)
        }
    }

    fun updateReserves(data: Reserves){
        viewModelScope.launch {
            reservesRepository.updateReserves(data)
        }
    }
}