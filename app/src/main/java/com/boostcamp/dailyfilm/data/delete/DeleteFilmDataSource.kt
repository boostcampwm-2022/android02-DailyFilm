package com.boostcamp.dailyfilm.data.delete

import android.net.Uri
import com.boostcamp.dailyfilm.data.model.Result

interface DeleteFilmDataSource {
    suspend fun deleteVideo(uploadDate: String, videoUri: Uri): Result<Unit>
}