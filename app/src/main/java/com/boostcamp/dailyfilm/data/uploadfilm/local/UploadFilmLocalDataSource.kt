package com.boostcamp.dailyfilm.data.uploadfilm.local

import android.net.Uri
import com.boostcamp.dailyfilm.data.model.CachedVideoEntity
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmDataSource
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UploadFilmLocalDataSource(
    private val localUriDao: LocalUriDao
) : UploadFilmDataSource {

    override fun uploadVideo(uploadDate: String, videoUri: Uri): Flow<Result<Uri?>> = callbackFlow {
        runCatching {
            localUriDao.insert(CachedVideoEntity(videoUri.toString(), uploadDate.toInt()))
        }.onSuccess {
            trySend(Result.Success(videoUri))
        }.onFailure { exception ->
            trySend(Result.Error(exception))
        }

        awaitClose()
    }

}
