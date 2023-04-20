package com.example.myapplication.data.source.remote.response.entity

import com.example.myapplication.data.source.local.entity.room.master.Purchase
import com.example.myapplication.data.source.local.entity.room.master.Supplier

data class PurchaseResponse(
    var suppliers: List<Supplier>,
    var purchases: List<Purchase>
){
    constructor(): this(emptyList(), emptyList())
}