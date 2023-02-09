package com.boostcamp.dailyfilm.data.uploadfilm

import android.net.Uri
import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.remote.UploadFilmRemoteDataSource
import javax.inject.Inject

interface UploadFilmRepository {
    suspend fun uploadVideo(uploadDate: String, videoUri: Uri): Result<Uri?>
    suspend fun uploadFilmInfo(uploadDate: String, filmInfo: DailyFilmItem): Result<Unit>
    suspend fun uploadEditVideo(uploadDate: String, item: DailyFilmItem): Result<Unit>
    suspend fun insertFilmEntity(filmInfo: DailyFilmItem)
}

class UploadFilmRepositoryImpl @Inject constructor(
    private val uploadFilmLocalDataSource: UploadFilmDataSource,
    private val uploadFilmRemoteDataSource: UploadFilmDataSource,
    private val calendarDataSource: CalendarDataSource
) : UploadFilmRepository {

    override suspend fun uploadVideo(uploadDate: String, videoUri: Uri): Result<Uri?> {
        val localResult = uploadFilmLocalDataSource.uploadVideo(uploadDate, videoUri)
        val remoteResult = uploadFilmRemoteDataSource.uploadVideo(uploadDate, videoUri)

        return if (localResult is Result.Success && remoteResult is Result.Success) {
            Result.Success(remoteResult.data)
        } else {
            Result.Error(Exception("There is a failure in upload process"))
        }
    }

    override suspend fun uploadEditVideo(uploadDate: String, item: DailyFilmItem): Result<Unit> {
        val remoteResult = uploadFilmInfo(uploadDate, item)

        return if (remoteResult is Result.Success) {
            insertFilmEntity(item)
            Result.Success(remoteResult.data)
        } else {
            Result.Error(Exception("There is a failure in upload process"))
        }
    }

    override suspend fun uploadFilmInfo(uploadDate: String, filmInfo: DailyFilmItem) =
        (uploadFilmRemoteDataSource as UploadFilmRemoteDataSource).uploadFilmInfo(
            uploadDate,
            filmInfo
        )

    override suspend fun insertFilmEntity(filmInfo: DailyFilmItem) {
        calendarDataSource.insertFilm(filmInfo.mapToFilmEntity())
    }
}
