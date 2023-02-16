package com.boostcamp.dailyfilm.data.delete

import android.net.Uri
import androidx.core.net.toUri
import com.boostcamp.dailyfilm.data.delete.remote.DeleteFilmRemoteDataSource
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result

interface DeleteFilmRepository {
    suspend fun delete(deleteDate: String): Result<Unit>
    suspend fun deleteVideo(uploadDate: String, videoUri: Uri): Result<Unit>
    suspend fun deleteFilmInfo(deleteDate: String): Result<DailyFilmItem?>
}

class DeleteFilmRepositoryImpl(
    private val deleteFilmLocalDataSource: DeleteFilmDataSource,
    private val deleteFilmRemoteDataSource: DeleteFilmDataSource,
) : DeleteFilmRepository {
    override suspend fun delete(deleteDate: String): Result<Unit> {
        when (val result = deleteFilmInfo(deleteDate)) {
            is Result.Success -> {
                val item = result.data
                    ?: return Result.Error(Exception("There is a failure in delete process"))
                return deleteVideo(deleteDate, item.videoUrl.toUri())
            }
            is Result.Error -> {
                return Result.Error(result.exception)
            }
        }
    }

    override suspend fun deleteVideo(uploadDate: String, videoUri: Uri): Result<Unit> {
        val localResult = deleteFilmLocalDataSource.deleteVideo(uploadDate, videoUri)
        val remoteResult = deleteFilmRemoteDataSource.deleteVideo(uploadDate, videoUri)

        return if (localResult is Result.Success && remoteResult is Result.Success) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception("There is a failure in delete process"))
        }
    }

    override suspend fun deleteFilmInfo(deleteDate: String) =
        (deleteFilmRemoteDataSource as DeleteFilmRemoteDataSource).deleteFilm(deleteDate)
}