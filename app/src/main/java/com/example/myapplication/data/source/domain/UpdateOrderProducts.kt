package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.helper.ProductOrderDetail

interface UpdateOrderProducts {
    suspend operator fun invoke(orderNo: String, products: List<ProductOrderDetail>)
}
