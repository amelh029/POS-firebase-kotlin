package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail
import kotlinx.coroutines.flow.Flow

interface GetProductOrder {
    operator fun invoke(orderNo: String): Flow<List<ProductOrderDetail>>
}
