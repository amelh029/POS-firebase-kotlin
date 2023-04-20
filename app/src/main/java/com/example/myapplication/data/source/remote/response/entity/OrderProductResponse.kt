package com.example.myapplication.data.source.remote.response.entity

import com.example.myapplication.data.source.local.entity.room.bridge.OrderDetail
import com.example.myapplication.data.source.local.entity.room.bridge.OrderMixProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariantMix

data class OrderProductResponse (
    var products: DataProductResponse,
    var details: List<OrderDetail>,
    var mixOrders: List<OrderProductVariantMix>,
    var mixVariants: List<OrderMixProductVariant>,
    var variants: List<OrderProductVariant>
){
    constructor(): this(DataProductResponse(), emptyList(), emptyList(), emptyList(), emptyList())
}