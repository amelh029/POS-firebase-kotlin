package com.example.myapplication.data.source.local.entity.room.helper

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myapplication.data.source.local.entity.room.bridge.VariantProduct
import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import java.io.Serializable


data class VariantProductWithOption(
    @Embedded val variantProduct: VariantProduct,

    @Relation(
        parentColumn = Variant.ID,
        entityColumn = Variant.ID,
    ) val variant: Variant,

    @Relation(
        parentColumn = VariantOption.ID,
        entityColumn = VariantOption.ID,
    ) val option: VariantOption
) : Serializable
