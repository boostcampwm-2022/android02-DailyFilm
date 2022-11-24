package com.boostcamp.dailyfilm.data.calendar

import com.boostcamp.dailyfilm.BuildConfig
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.google.firebase.database.ktx.ChildEvent
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CalendarDataSource {
    fun loadFilmInfo(userId: String, startAt: String, endAt: String): Flow<DailyFilmItem?>
}

class CalendarDataSourceImpl : CalendarDataSource {
    override fun loadFilmInfo(
        userId: String,
        startAt: String,
        endAt: String
    ): Flow<DailyFilmItem?> =
        database.reference
            .child(DIRECTORY_USER)
            .child(userId)
            .orderByKey()
            .startAfter(startAt)
            .endAt(endAt)
            .childEvents.map { event ->
                when (event) {
                    is ChildEvent.Added -> {
                        event.snapshot.getValue(DailyFilmItem::class.java)
                    }
                    is ChildEvent.Changed -> {
                        null
                    }
                    is ChildEvent.Moved -> {
                        null
                    }
                    is ChildEvent.Removed -> {
                        null
                    }
                }
            }

    companion object {
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}
