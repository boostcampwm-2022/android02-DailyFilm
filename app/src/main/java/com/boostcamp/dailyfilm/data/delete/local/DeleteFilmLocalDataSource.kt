package com.boostcamp.dailyfilm.data.delete.local

import android.net.Uri
import com.boostcamp.dailyfilm.data.delete.DeleteFilmDataSource
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao

class DeleteFilmLocalDataSource(
    private val localUriDao: LocalUriDao
) : DeleteFilmDataSource {
    override suspend fun deleteVideo(uploadDate: String, videoUri: Uri): Result<Unit> {
        runCatching {
            localUriDao.deleteFilm(uploadDate.toInt())
            localUriDao.deleteVideoFilm(uploadDate.toInt())
        }.onSuccess {
            return Result.Success(Unit)
        }.onFailure { exception ->
            return Result.Error(exception)
        }
        return Result.Error(Error())
    }
}