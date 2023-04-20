package com.example.myapplication.data.source.local.entity.helper

import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.data.source.local.entity.room.master.VariantOption

data class VariantWithOptions(
    val variant: Variant,
    val options: List<VariantOption>
) {
    fun optionsString() = options.joinToString { it.name }
    fun isOptionAvailable() = options.any { it.isActive }
}
