package com.boostcamp.dailyfilm.data.selectvideo.remote

import android.util.Log
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface GalleryDataRemoteSource {
    fun uploadVideo(videoItem: VideoItem): Flow<Boolean>
}


class GalleryDataRemoteSourceImpl: GalleryDataRemoteSource {

    override fun uploadVideo(videoItem: VideoItem) = callbackFlow {
        val uri = videoItem.uri
        val reference = storage.reference // File Pointer
        val videoRef = reference.child("user_videos/${uri.lastPathSegment}") // TODO 선택한 날짜값을 이름으로 재구성할 것
        val metadata = storageMetadata {
            contentType = "video/mp4"
        }
        videoRef.putFile(uri, metadata)
            .addOnFailureListener{
                trySend(false).isFailure
                Log.d("RemoteSource", it.message.toString())
            }
            .addOnSuccessListener {
                trySend(true).isSuccess
                Log.d("RemoteSource", "Success")
            }

        awaitClose()
    }


    companion object{
        val storage = Firebase.storage
    }
}