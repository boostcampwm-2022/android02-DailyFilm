package com.boostcamp.dailyfilm.presentation.login

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityLoginBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.util.LottieDialogFragment
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels()
    private val loadingDialogFragment by lazy { LottieDialogFragment() }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun initView() {
        setGoogleLogin()
        //setObserveLoginResult()
    }

    override fun onStart() {
        super.onStart()
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.btnLoginGoogleAuth.setColorScheme(SignInButton.COLOR_DARK)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.btnLoginGoogleAuth.setColorScheme(SignInButton.COLOR_LIGHT)
            }
        }
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }
    }

/*    private fun setObserveLoginResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is UiState.Uninitialized -> {
                            return@collectLatest
                        }
                        is UiState.Success -> {
                            loadingDialogFragment.hideProgressDialog()
                            startActivity(Intent(this@LoginActivity, CalendarActivity::class.java))
                        }
                        is UiState.Loading -> {
                            loadingDialogFragment.showProgressDialog(supportFragmentManager)
                        }
                        is UiState.Failure -> {
                            loadingDialogFragment.hideProgressDialog()
                            state.throwable.message?.let { showSnackBarMessage(it) }
                        }
                    }
                }
            }
        }
    }*/

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setGoogleLogin() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        val client = GoogleSignIn.getClient(this, options)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.requestLogin(account.idToken!!)
                } catch (e: ApiException) {
                    showSnackBarMessage("Failed Google Login")
                }
            }
        }

        binding.btnLoginGoogleAuth.setOnClickListener {
            activityResultLauncher.launch(client.signInIntent)
        }
    }

}

