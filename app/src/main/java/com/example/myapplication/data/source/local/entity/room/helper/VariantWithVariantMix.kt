package com.example.myapplication.data.source.local.entity.room.helper

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.myapplication.data.source.local.entity.room.bridge.VariantMix
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.Variant

data class VariantWithVariantMix(
    @Embedded
    var variant: Variant? = null,

    @Relation(
        parentColumn = Variant.ID,
        entity = Product::class,
        entityColumn = Product.ID,
        associateBy = Junction(
            value = VariantMix::class,
            parentColumn = Variant.ID,
            entityColumn = Product.ID
        )
    ) val products: List<Product>
)
