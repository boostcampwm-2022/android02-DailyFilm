package com.boostcamp.dailyfilm.data.delete

import android.net.Uri
import com.boostcamp.dailyfilm.data.delete.remote.DeleteFilmRemoteDataSource
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip

interface DeleteFilmRepository {
    fun deleteVideo(uploadDate: String, videoUri: Uri): Flow<Result<Unit>>
    fun deleteFilmInfo(deleteDate: String): Flow<Result<DailyFilmItem?>>
}

class DeleteFilmRepositoryImpl(
    private val deleteFilmLocalDataSource: DeleteFilmDataSource,
    private val deleteFilmRemoteDataSource: DeleteFilmDataSource,
) : DeleteFilmRepository {
    override fun deleteVideo(uploadDate: String, videoUri: Uri): Flow<Result<Unit>> {
        val localFlow = deleteFilmLocalDataSource.deleteVideo(uploadDate, videoUri)
        val remoteFlow = deleteFilmRemoteDataSource.deleteVideo(uploadDate, videoUri)

        return localFlow.zip(remoteFlow) { localResult, remoteResult ->
            when {
                localResult is Result.Success && remoteResult is Result.Success -> {
                    Result.Success(remoteResult.data)
                }
                else -> Result.Error(Exception("There is a failure in upload process"))
            }
        }
    }

    override fun deleteFilmInfo(deleteDate: String) =
        (deleteFilmRemoteDataSource as DeleteFilmRemoteDataSource).deleteFilm(deleteDate)
}