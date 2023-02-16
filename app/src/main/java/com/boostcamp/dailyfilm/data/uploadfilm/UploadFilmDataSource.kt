package com.boostcamp.dailyfilm.data.uploadfilm


import android.net.Uri
import com.boostcamp.dailyfilm.data.model.Result

interface UploadFilmDataSource {
    suspend fun uploadVideo(uploadDate: String, videoUri: Uri): Result<Uri?>

}