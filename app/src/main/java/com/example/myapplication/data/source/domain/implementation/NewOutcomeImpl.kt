package com.example.myapplication.data.source.domain.implementation

import com.example.myapplication.data.source.domain.NewOutcome
import com.example.myapplication.data.source.local.entity.room.master.Outcome
import com.example.myapplication.data.source.repository.OutcomesRepository
import com.example.myapplication.data.source.repository.SettingRepository
import kotlinx.coroutines.flow.first

class NewOutcomeImpl(
    private val settingRepository: SettingRepository,
    private val outcomesRepository: OutcomesRepository
) : NewOutcome {
    override suspend fun invoke(outcome: Outcome) {
        val store = settingRepository.getSelectedStore().first()
        outcomesRepository.insertOutcome(outcome.copy(
            store = store
        ))
    }
}
