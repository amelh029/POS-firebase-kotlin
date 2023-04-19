package com.example.myapplication.view.viewModel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.utils.tools.ViewModelFactory

open class ViewModelFromFactory<T : ViewModel> {
    protected fun buildViewModel(activity: FragmentActivity, modelClass: Class<T>): T {
        return getProvider(activity).get(modelClass)
    }

    private fun getProvider(activity: FragmentActivity): ViewModelProvider {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.applicationContext)
        return ViewModelProvider(activity, factory)
    }

}