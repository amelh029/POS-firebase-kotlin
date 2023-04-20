package com.example.myapplication.data.source.domain

import com.example.myapplication.data.source.local.entity.room.master.Outcome


interface NewOutcome {
    suspend operator fun invoke(outcome: Outcome)
}
