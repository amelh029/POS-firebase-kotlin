package com.example.myapplication.data.source.remote.response.helper

import com.google.firebase.firestore.QuerySnapshot

interface RemoteClassUtils<T> {
    fun toHashMap(data: T): HashMap<String, Any?>
    fun toListClass(result: QuerySnapshot): List<T>
}