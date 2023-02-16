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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DeleteFilmRemoteDataSource : DeleteFilmDataSource {
    override suspend fun deleteVideo(uploadDate: String, videoUri: Uri): Result<Unit> =
        suspendCoroutine { continuation ->
            val reference = storage.reference
            val videoRef = reference.child("${videoUri.lastPathSegment}")

            videoRef.delete()
                .addOnSuccessListener {
                    continuation.resume(Result.Success(Unit))
                }.addOnFailureListener { exception ->
                    continuation.resume(Result.Error(exception))
                }
        }

    suspend fun deleteFilm(uploadDate: String) =
        suspendCoroutine { continuation ->
            userId?.let { id ->
                val reference = database.reference
                    .child(DIRECTORY_USER)
                    .child(id)
                    .child(uploadDate)

                reference.get()
                    .addOnSuccessListener { snapshot ->
                        reference.removeValue()
                            .addOnSuccessListener {
                                continuation.resume(Result.Success(snapshot.getValue(DailyFilmItem::class.java)))
                            }
                            .addOnFailureListener { exception ->
                                continuation.resume(Result.Error(exception))
                            }
                    }.addOnFailureListener { exception ->
                        continuation.resume(Result.Error(exception))
                    }
            }
        }

    companion object {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storage = Firebase.storage

        // BuildConfig.BUILD_TYPE
        val database = Firebase.database(BuildConfig.DATABASE_URL)
        const val DIRECTORY_USER = "users"
    }
}