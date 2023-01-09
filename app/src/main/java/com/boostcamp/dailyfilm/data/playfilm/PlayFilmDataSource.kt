package com.boostcamp.dailyfilm.data.playfilm

import android.net.Uri
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.flow.Flow

interface PlayFilmDataSource {

    fun loadVideo(uploadDate: String): Flow<Result<Uri?>>

}