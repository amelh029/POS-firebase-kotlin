package com.example.myapplication.data.source.remote.response.entity

data class BatchWithObject<T>(
    var data: T,
    var batch: BatchWithData
)
