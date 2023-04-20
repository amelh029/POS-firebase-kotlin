package com.example.myapplication.data.source.local.entity.room.helper

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.myapplication.data.source.local.entity.room.bridge.OrderMixProductVariant
import com.example.myapplication.data.source.local.entity.room.bridge.OrderProductVariantMix
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import java.io.Serializable

data class DetailProductMixWithVariantOption(
    @Embedded
    var mix: OrderProductVariantMix,

    @Relation(
        parentColumn = OrderProductVariantMix.ID,
        entity = VariantOption::class,
        entityColumn = VariantOption.ID,
        associateBy = Junction(
            value = OrderMixProductVariant::class,
            parentColumn = OrderProductVariantMix.ID,
            entityColumn = VariantOption.ID
        )
    ) val options: List<VariantOption> = emptyList()
): Serializable
