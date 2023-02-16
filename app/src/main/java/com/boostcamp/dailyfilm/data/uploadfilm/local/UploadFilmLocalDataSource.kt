package com.boostcamp.dailyfilm.data.uploadfilm.local

import android.net.Uri
import com.boostcamp.dailyfilm.data.model.CachedVideoEntity
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmDataSource

class UploadFilmLocalDataSource(
    private val localUriDao: LocalUriDao
) : UploadFilmDataSource {

    override suspend fun uploadVideo(uploadDate: String, videoUri: Uri): Result<Uri?> {
        runCatching {
            localUriDao.insert(CachedVideoEntity(videoUri.toString(), uploadDate.toInt()))
        }.onSuccess {
            return Result.Success(videoUri)
        }.onFailure { exception ->
            return Result.Error(exception)
        }
        return Result.Error(Error())
    }
}
