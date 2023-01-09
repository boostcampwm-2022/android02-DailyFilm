package com.boostcamp.dailyfilm.data.playfilm.local

import android.net.Uri
import android.util.Log
import com.boostcamp.dailyfilm.data.model.CachedVideoEntity
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PlayFilmLocalDataSource(
    private val localUriDao: LocalUriDao
) : PlayFilmDataSource {

    override fun loadVideo(uploadDate: String): Flow<Result<Uri?>> = callbackFlow {

        runCatching {
            localUriDao.loadFilm(uploadDate.toInt())
        }.onSuccess {
            if (it == null) {
                trySend(Result.Success(null))
            } else {
                trySend(Result.Success(Uri.parse(it.localUri)))
            }
        }.onFailure { exception ->
            trySend(Result.Error(exception))
        }

        awaitClose()
    }

    fun insertVideo(uploadDate: String, localUri: String): Flow<Result<Unit>> = callbackFlow {

        runCatching {
            localUriDao.insert(CachedVideoEntity(localUri, uploadDate.toInt()))
        }.onSuccess {
            trySend(Result.Success(Unit))
        }.onFailure { exception ->
            trySend(Result.Error(exception))
        }

        awaitClose()
    }
}