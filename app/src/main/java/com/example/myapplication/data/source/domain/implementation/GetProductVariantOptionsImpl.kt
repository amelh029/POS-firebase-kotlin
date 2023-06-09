package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.GetProductVariantOptions
import com.example.myapplication.data.source.local.entity.helper.VariantWithOptions
import com.example.myapplication.data.source.local.entity.room.helper.VariantProductWithOption
import com.example.myapplication.data.source.local.room.ProductVariantsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetProductVariantOptionsImpl(
    private val dao: ProductVariantsDao
) : GetProductVariantOptions {
    override fun invoke(idProduct: Long): Flow<List<VariantWithOptions>?> {
        return dao.getVariantProducts(idProduct)
            .map { it?.handleVariants() }
    }

    private fun List<VariantProductWithOption>.handleVariants() =
        this.groupBy { it.variant }
            .map {
                VariantWithOptions(
                    variant = it.key,
                    options = it.value.map { option ->
                        option.option
                    }
                )
            }
}
