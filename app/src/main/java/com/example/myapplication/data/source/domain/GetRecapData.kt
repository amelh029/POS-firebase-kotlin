package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.helper.RecapData
import com.example.myapplication.utils.tools.helper.ReportsParameter
import kotlinx.coroutines.flow.Flow

interface GetRecapData {
    operator fun invoke(parameters: ReportsParameter): Flow<RecapData>
}
