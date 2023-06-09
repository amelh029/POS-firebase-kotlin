package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.helper.VariantWithOptions
import kotlinx.coroutines.flow.Flow

interface GetProductVariantOptions {
    operator fun invoke(idProduct: Long): Flow<List<VariantWithOptions>?>
}
