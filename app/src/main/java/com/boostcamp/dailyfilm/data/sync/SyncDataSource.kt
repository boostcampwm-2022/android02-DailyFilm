package com.boostcamp.dailyfilm.data.sync

import com.boostcamp.dailyfilm.BuildConfig
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface SyncDataSource {
    suspend fun loadFilmInfo(userId: String, startAt: String, endAt: String): List<DailyFilmItem?>?
}

class SyncRemoteDataSource : SyncDataSource {

    override suspend fun loadFilmInfo(
        userId: String,
        startAt: String,
        endAt: String
    ): List<DailyFilmItem?>? = suspendCoroutine { continuation ->
        database.reference
            .child(DIRECTORY_USER)
            .child(userId)
            .orderByKey()
            .startAt(startAt)
            .endAt(endAt)
            .get()
            .addOnSuccessListener { snapshot ->
                continuation.resume(
                    snapshot.children.map {
                        it.getValue(DailyFilmItem::class.java)
                    }
                )
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    companion object {
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}
