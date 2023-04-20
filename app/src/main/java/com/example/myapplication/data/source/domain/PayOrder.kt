package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.room.bridge.OrderPayment
import com.example.myapplication.data.source.local.entity.room.bridge.OrderPromo
import com.example.myapplication.data.source.local.entity.room.master.Order

interface PayOrder {
    suspend operator fun invoke(order: Order, payment: OrderPayment, promo: OrderPromo?)
}
