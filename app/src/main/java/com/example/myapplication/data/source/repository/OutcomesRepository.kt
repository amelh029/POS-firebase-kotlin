package com.example.myapplication.data.source.repository

import kotlinx.coroutines.flow.Flow

interface OutcomesRepository {

    fun getOutcomes(date: String): Flow<List<Outcome>>
    fun getOutcomes(parameters: ReportsParameter): Flow<List<Outcome>>
    suspend fun insertOutcome(data: Outcome)
    suspend fun updateOutcome(data: Outcome)
}
