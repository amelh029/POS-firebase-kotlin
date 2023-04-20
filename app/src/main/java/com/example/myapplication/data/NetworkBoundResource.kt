@file:Suppress("LeakingThis")

package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.myapplication.utils.database.AppExecutors
import com.example.myapplication.utils.tools.helper.Resource

abstract class NetworkBoundResource<ResultType, RequestType> protected constructor(private val mExecutors: AppExecutors) {

    protected abstract fun loadFromDB(): LiveData<ResultType>

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        val dbSource = loadFromDB()
        result.addSource(dbSource) { data: ResultType ->
            result.value = Resource.success(data)
        }
    }

    private fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }
}
