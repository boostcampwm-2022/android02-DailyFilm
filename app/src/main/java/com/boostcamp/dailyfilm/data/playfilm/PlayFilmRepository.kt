package com.boostcamp.dailyfilm.data.playfilm

import android.net.Uri
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.playfilm.local.PlayFilmLocalDataSource
import kotlinx.coroutines.flow.Flow

interface PlayFilmRepository {

    fun checkVideo(uploadDate: String): Flow<Result<Uri?>>

    fun downloadVideo(uploadDate: String): Flow<Result<Uri?>>

    fun insertVideo(uploadDate: String, localUri: String): Flow<Result<Unit>>
}

class PlayFilmRepositoryImpl(
    private val playFilmLocalDataSource: PlayFilmDataSource,
    private val playFilmRemoteDataSource: PlayFilmDataSource
): PlayFilmRepository {

    override fun checkVideo(uploadDate: String): Flow<Result<Uri?>> =
        playFilmLocalDataSource.loadVideo(uploadDate)

    override fun downloadVideo(uploadDate: String): Flow<Result<Uri?>> =
        playFilmRemoteDataSource.loadVideo(uploadDate) // load url

    override fun insertVideo(uploadDate: String, localUri: String): Flow<Result<Unit>> =
        (playFilmLocalDataSource as PlayFilmLocalDataSource).insertVideo(uploadDate, localUri)
}