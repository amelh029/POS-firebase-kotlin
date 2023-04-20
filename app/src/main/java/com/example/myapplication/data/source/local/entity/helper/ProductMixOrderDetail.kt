package com.example.myapplication.data.source.local.entity.helper

import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import java.io.Serializable

data class ProductMixOrderDetail(
    var product: Product,
    var variants: List<VariantOption>,
    var amount: Int,
): Serializable
