package com.example.myapplication.data.source.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.entity.room.master.User
import com.example.myapplication.utils.tools.helper.Resource

internal interface POSDataSource {
    fun updateOrder(order: Order)
    fun replaceProductOrder(old: OrderWithProduct, new: OrderWithProduct)

    fun getUsers(userId: String): LiveData<Resource<User?>>
}
