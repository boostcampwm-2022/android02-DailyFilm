package com.boostcamp.dailyfilm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_video_entity")
data class CachedVideoEntity(
    val localUri: String,
    @PrimaryKey val updateDate: Int = 0
)
