package com.boostcamp.dailyfilm.data.calendar

import android.util.Log
import com.boostcamp.dailyfilm.BuildConfig
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


interface CalendarDataSource {
    fun loadFilmInfo(userId: String, startAt: String, endAt: String): Flow<List<DailyFilmItem?>>
}

class CalendarDataSourceImpl : CalendarDataSource {
    override fun loadFilmInfo(
        userId: String,
        startAt: String,
        endAt: String
    ): Flow<List<DailyFilmItem?>> =
        callbackFlow {
            database.reference.child(DIRECTORY_USER)
                .child(userId)
                .orderByKey()
                .startAfter(startAt)
                .endAt(endAt)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = mutableListOf<DailyFilmItem?>()
                    snapshot.children.forEach {
                        val dailyItem = it.getValue(DailyFilmItem::class.java)
                        Log.d("CalendarDataSourceImpl", "loadFilmInfo: $dailyItem")
                        list.add(dailyItem)
                    }
                    trySend(list)
                }
                .addOnFailureListener {
                    trySend(listOf())
                    // TODO : Exception 처리
                }

            awaitClose()
        }

    companion object {
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}