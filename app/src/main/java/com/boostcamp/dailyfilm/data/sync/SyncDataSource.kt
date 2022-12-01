package com.boostcamp.dailyfilm.data.sync

import com.boostcamp.dailyfilm.BuildConfig
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SyncDataSource {
    fun loadFilmInfo(userId: String, startAt: String, endAt: String): Flow<List<DailyFilmItem?>?>
}

class SyncRemoteDataSource : SyncDataSource {

    override fun loadFilmInfo(
        userId: String,
        startAt: String,
        endAt: String
    ): Flow<List<DailyFilmItem?>?> = callbackFlow {
        database.reference
            .child(DIRECTORY_USER)
            .child(userId)
            .orderByKey()
            .startAfter(startAt)
            .endAt(endAt)
            .get()
            .addOnSuccessListener { snapshot ->
                trySend(
                    snapshot.children.map {
                        it.getValue(DailyFilmItem::class.java)
                    }
                )
            }
            .addOnFailureListener {
                trySend(null)
            }
        awaitClose()
    }

    companion object {
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}
