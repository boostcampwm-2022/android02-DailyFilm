package com.boostcamp.dailyfilm.data.uploadfilm


import android.net.Uri
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.flow.Flow

interface UploadFilmDataSource {
    fun uploadVideo(uploadDate: String, videoUri: Uri): Flow<Result<Uri?>>

}