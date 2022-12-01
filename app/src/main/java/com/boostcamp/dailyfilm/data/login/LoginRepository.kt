package com.boostcamp.dailyfilm.data.login

import android.util.Log
import com.boostcamp.dailyfilm.data.model.Result
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.security.auth.login.LoginException

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
            when(exception){
                is FirebaseNetworkException ->{
                    Log.d("errorCodeCheck" ,"FirebaseNetworkException ${exception.message}")
                }
                is FirebaseAuthException ->{
                    Log.d("errorCodeCheck" ,"FirebaseAuthException ${exception.errorCode}")
                }
            }
            trySend(Result.Error(exception))
        }
        awaitClose()
    }
}