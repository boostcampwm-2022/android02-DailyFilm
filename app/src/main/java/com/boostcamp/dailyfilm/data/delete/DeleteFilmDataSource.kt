package com.boostcamp.dailyfilm.data.delete

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import com.boostcamp.dailyfilm.data.model.Result

interface DeleteFilmDataSource {
    fun deleteVideo(uploadDate: String, videoUri: Uri): Flow<Result<Unit>>
}