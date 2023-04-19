package com.example.myapplication.view.viewModel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.source.repository.POSRepository
import com.example.myapplication.utils.tools.helper.Resource

class UserViewModel (private val repository: POSRepository) : ViewModel()  {
    companion object : ViewModelFromFactory<UserViewModel>() {
        fun getMainViewModel(activity: FragmentActivity): UserViewModel {
            return buildViewModel(activity, UserViewModel::class.java)
        }
    }

    fun getUser(userId: String): LiveData<Resource<User?>> {
        return repository.getUsers(userId)
    }
}