package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import com.example.myapplication.data.source.local.entity.room.master.Customer

interface NewOrder {
    suspend operator fun invoke(
        customer: Customer,
        isTakeAway: Boolean,
        products: List<ProductOrderDetail>,
        currentTime: String
    )
}
