package com.boostcamp.dailyfilm.data.login

import com.boostcamp.dailyfilm.data.model.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface LoginRepository {
    fun requestLogin(idToken: String): Flow<Result<FirebaseUser?>>
}

class LoginRepositoryImpl @Inject constructor() : LoginRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun requestLogin(idToken: String): Flow<Result<FirebaseUser?>> = callbackFlow {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(Result.Success(firebaseAuth.currentUser))
            }
        }.addOnFailureListener { exception ->
            trySend(Result.Error(exception))
        }
        awaitClose()
    }
}