package com.boostcamp.dailyfilm.data.uploadfilm

import android.net.Uri
import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UploadFilmRepository {
    fun uploadVideo(videoUri: Uri): Flow<Result<Uri?>>

    fun uploadFilmInfo(userId: String, uploadDate:String, filmInfo: DailyFilmItem): Flow<Result<Unit>>

    suspend fun insertFilmEntity(filmInfo: DailyFilmItem)
}

class UploadFilmRepositoryImpl @Inject constructor(
    private val uploadFilmDataSource: UploadFilmDataSource,
    private val calendarDataSource: CalendarDataSource
) : UploadFilmRepository {

    override fun uploadVideo(videoUri: Uri) = uploadFilmDataSource.uploadVideo(videoUri)

    override fun uploadFilmInfo(userId: String, uploadDate: String, filmInfo: DailyFilmItem) =
        uploadFilmDataSource.uploadFilmInfo(userId, uploadDate, filmInfo)

    override suspend fun insertFilmEntity(filmInfo: DailyFilmItem) {
        calendarDataSource.insertFilm(filmInfo.mapToFilmEntity())
    }
}
