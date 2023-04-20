package com.example.myapplication.data.source.remote.response.entity

import com.example.myapplication.data.source.local.entity.room.master.PurchaseProduct

data class PurchaseProductResponse(
    var purchases: List<PurchaseProduct>,
    var products: DataProductResponse
){
    constructor(): this(emptyList(), DataProductResponse())
}