package com.boostcamp.dailyfilm.data.repository.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepository @Inject constructor() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun requestLogin(idToken: String): Flow<FirebaseUser?> = callbackFlow {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(firebaseAuth.currentUser).isSuccess
            } else {
                trySend(null).isFailure
            }
        }
        awaitClose()
    }
}