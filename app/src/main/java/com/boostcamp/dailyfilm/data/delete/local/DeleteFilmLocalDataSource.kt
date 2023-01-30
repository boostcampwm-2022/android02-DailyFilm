package com.boostcamp.dailyfilm.data.delete.local

import android.net.Uri
import com.boostcamp.dailyfilm.data.delete.DeleteFilmDataSource
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DeleteFilmLocalDataSource(
    private val localUriDao: LocalUriDao
) : DeleteFilmDataSource {
    override fun deleteVideo(uploadDate: String, videoUri: Uri): Flow<Result<Unit>> = callbackFlow {
        runCatching {
            localUriDao.deleteFilm(uploadDate.toInt())
            localUriDao.deleteVideoFilm(uploadDate.toInt())
        }.onSuccess {
            trySend(Result.Success(Unit))
        }.onFailure { exception ->
            trySend(Result.Error(exception))
        }
        awaitClose()
    }
}