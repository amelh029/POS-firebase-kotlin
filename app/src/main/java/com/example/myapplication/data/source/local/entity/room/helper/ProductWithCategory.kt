package com.example.myapplication.data.source.local.entity.room.helper

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Product

data class ProductWithCategory(
    @Embedded
    var product: Product,

    @Relation(parentColumn = Category.ID, entityColumn = Category.ID)
    var category: Category,
)
