package com.boostcamp.dailyfilm.presentation.login

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityLoginBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var client: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun initView() {
        setGoogleLogin()
    }

    private fun setGoogleLogin() {
        // 요청 정보 옵션
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        client = GoogleSignIn.getClient(this, options)
        auth = FirebaseAuth.getInstance()

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!.idToken)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Failed Google Login", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLoginGoogleAuth.setOnClickListener {
            // 로그인 요청
            activityResultLauncher.launch(client.signInIntent)
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    // 인증에 성공한 후, 현재 로그인된 유저의 정보를 가져올 수 있습니다.
                    val email = auth.currentUser?.email
                    Toast.makeText(this, "email = $email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}