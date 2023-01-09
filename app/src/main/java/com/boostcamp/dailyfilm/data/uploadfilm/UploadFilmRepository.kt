package com.boostcamp.dailyfilm.data.uploadfilm

import android.net.Uri
import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.remote.UploadFilmRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

interface UploadFilmRepository {
    fun uploadVideo(uploadDate: String, videoUri: Uri): Flow<Result<Uri?>>

    fun uploadFilmInfo(uploadDate:String, filmInfo: DailyFilmItem): Flow<Result<Unit>>

    suspend fun insertFilmEntity(filmInfo: DailyFilmItem)
}

class UploadFilmRepositoryImpl @Inject constructor(
    private val uploadFilmLocalDataSource: UploadFilmDataSource,
    private val uploadFilmRemoteDataSource: UploadFilmDataSource,
    private val calendarDataSource: CalendarDataSource
) : UploadFilmRepository {

    override fun uploadVideo(uploadDate: String, videoUri: Uri): Flow<Result<Uri?>> {
        val localFlow = uploadFilmLocalDataSource.uploadVideo(uploadDate, videoUri)
        val remoteFlow = uploadFilmRemoteDataSource.uploadVideo(uploadDate, videoUri)

        return localFlow.zip(remoteFlow) { localResult, remoteResult ->
            when {
                localResult is Result.Success && remoteResult is Result.Success -> {
                    Result.Success(remoteResult.data)
                }
                else -> Result.Error(Exception("There is a failure in upload process"))
            }
        }
    }

    override fun uploadFilmInfo(uploadDate: String, filmInfo: DailyFilmItem) =
        (uploadFilmRemoteDataSource as UploadFilmRemoteDataSource).uploadFilmInfo(uploadDate, filmInfo)

    override suspend fun insertFilmEntity(filmInfo: DailyFilmItem) {
        calendarDataSource.insertFilm(filmInfo.mapToFilmEntity())
    }
}
