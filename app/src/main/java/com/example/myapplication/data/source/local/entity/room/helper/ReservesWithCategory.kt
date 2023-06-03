package com.example.myapplication.data.source.local.entity.room.helper

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory

data class ReservesWithCategory(
    @Embedded
    var reserves: Reserves,

    @Relation(parentColumn = ReservesCategory.ID, entityColumn = ReservesCategory.ID)
    var reservesCategory: ReservesCategory,
)