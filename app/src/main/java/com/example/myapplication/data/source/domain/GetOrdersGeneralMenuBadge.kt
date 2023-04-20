package com.example.myapplication.data.source.domain

import kotlinx.coroutines.flow.Flow

interface GetOrdersGeneralMenuBadge {
    operator fun invoke(date: String): Flow<Int>
}
