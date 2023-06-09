package com.example.myapplication.data.source.remote.response.entity

import com.example.myapplication.data.source.local.entity.room.bridge.OrderPayment
import com.example.myapplication.data.source.local.entity.room.master.Customer
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.entity.room.master.Payment

data class OrderResponse(
    var order: List<Order>,
    var payments: List<Payment>,
    var orderPayment: List<OrderPayment>,
    var customer: List<Customer>
){
    constructor(): this(emptyList(), emptyList(), emptyList(), emptyList())
}