package com.boostcamp.dailyfilm.data.playfilm.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.remote.UploadFilmRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject

class PlayFilmRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayFilmDataSource {

    override fun loadVideo(uploadDate: String): Flow<Result<Uri>> = callbackFlow {
        userId?.let { id ->
            val urlRef = UploadFilmRemoteDataSource.database.reference
                .child(DIRECTORY_USER)
                .child(id)
                .child(uploadDate)
                .child(VIDEO_URL)

            urlRef.get()
                .addOnSuccessListener { snapshot ->
                    snapshot.value ?: return@addOnSuccessListener

                    val videoUrl = snapshot.value.toString()
                    val storageReference = storage.getReferenceFromUrl(videoUrl)
                    val file = File(context.filesDir, "$uploadDate.mp4")
                    Log.d("LoadVideo", "absolutePath : ${file.absolutePath}")

                    storageReference.getFile(file)
                        .addOnSuccessListener {
                            trySend(Result.Success(file.toUri()))
                        }.addOnFailureListener { exception ->
                            trySend(Result.Error(exception))
                        }
                }
                .addOnFailureListener { exception ->
                    trySend(Result.Error(exception))
                }
        }

        awaitClose()
    }

    companion object {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storage = Firebase.storage
        const val DIRECTORY_USER = "users"
        const val VIDEO_URL = "videoUrl"
    }
}
