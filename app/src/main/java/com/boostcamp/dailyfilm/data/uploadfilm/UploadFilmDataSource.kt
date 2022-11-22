package com.boostcamp.dailyfilm.data.uploadfilm

import android.net.Uri
import android.util.Log
import com.boostcamp.dailyfilm.BuildConfig
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface UploadFilmDataSource {
    fun uploadVideo(videoUri: Uri): Flow<Uri?>

    fun uploadFilmInfo(userId: String, uploadDate:String, filmInfo: DailyFilmItem): Flow<Boolean>
}


class UploadFilmDataSourceImpl : UploadFilmDataSource {

    override fun uploadVideo(videoUri: Uri) = callbackFlow {
        val uri = videoUri
        val reference = storage.reference // File Pointer
        val videoRef =
            reference.child("user_videos/${uri.lastPathSegment}") // TODO 선택한 날짜값을 이름으로 재구성할 것
        val metadata = storageMetadata {
            contentType = "video/mp4"
        }

        videoRef.putFile(uri, metadata)
            .continueWithTask {
                videoRef.downloadUrl
            }
            .addOnSuccessListener {
                trySend(it)
            }.addOnFailureListener {
                trySend(null)
            }
        awaitClose()
    }

    override fun uploadFilmInfo(userId: String, uploadDate:String, filmInfo: DailyFilmItem) = callbackFlow {

        val reference = database.reference
            .child(DIRECTORY_USER)
            .child(userId)
            .child(uploadDate)

        reference.setValue(filmInfo)
            .addOnSuccessListener {
                trySend(true).isSuccess
            }
            .addOnFailureListener {
                trySend(false).isFailure
            }

        awaitClose()
    }

    companion object {
        val storage = Firebase.storage

        // BuildConfig.BUILD_TYPE
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}