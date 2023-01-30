package com.boostcamp.dailyfilm.data.delete.remote

import android.net.Uri
import com.boostcamp.dailyfilm.BuildConfig
import com.boostcamp.dailyfilm.data.delete.DeleteFilmDataSource
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DeleteFilmRemoteDataSource : DeleteFilmDataSource {
    override fun deleteVideo(uploadDate: String, videoUri: Uri): Flow<Result<Unit>> = callbackFlow {
        val reference = storage.reference
        val videoRef = reference.child("${videoUri.lastPathSegment}")

        videoRef.delete()
            .addOnSuccessListener {
                trySend(Result.Success(Unit))
            }.addOnFailureListener { exception ->
                trySend(Result.Error(exception))
            }
        awaitClose()
    }

    fun deleteFilm(uploadDate: String) =
        callbackFlow {
            userId?.let { id ->
                val reference = database.reference
                    .child(DIRECTORY_USER)
                    .child(id)
                    .child(uploadDate)

                reference.get()
                    .addOnSuccessListener { snapshot ->
                        reference.removeValue()
                            .addOnSuccessListener {
                                trySend(Result.Success(snapshot.getValue(DailyFilmItem::class.java)))
                            }
                            .addOnFailureListener { exception ->
                                trySend(Result.Error(exception))
                            }
                    }.addOnFailureListener { exception ->
                        trySend(Result.Error(exception))
                    }
            }
            awaitClose()
        }

    companion object {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storage = Firebase.storage

        // BuildConfig.BUILD_TYPE
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}