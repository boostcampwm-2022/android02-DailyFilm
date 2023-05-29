package com.boostcamp.dailyfilm.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.util.LottieDialogFragment
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginComposeActivity : ComponentActivity() {
    private val viewModel by viewModels<LoginViewModel>()
    private val loadingDialogFragment by lazy { LottieDialogFragment() }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContent {
            LoginView(client)
        }
        setObserveLoginResult()
    }

    @Composable
    private fun LoginView(client: GoogleSignInClient) {
        val backgroundColor = if (isSystemInDarkTheme()) {
            Color(0xFF4285F4)
        } else {
            Color(0xFFFFFFFF)
        }
        val contentsColor = if (isSystemInDarkTheme()) {
            Color(0xFFFFFFFF)
        } else {
            Color(0xFF454647)
        }
        Column {
            Logo()
            LoginButton(activityResultLauncher, client, backgroundColor, contentsColor)
        }
    }

    override fun onStart() {
        super.onStart()
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            startActivity(Intent(this, CalendarActivity::class.java))
            finish()
        }
    }

    private fun setObserveLoginResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is UiState.Uninitialized -> {
                            return@collectLatest
                        }

                        is UiState.Success -> {
                            loadingDialogFragment.hideProgressDialog()
                            startActivity(
                                Intent(
                                    this@LoginComposeActivity,
                                    CalendarActivity::class.java
                                )
                            )
                        }

                        is UiState.Loading -> {
                            // loadingDialogFragment.showProgressDialog(supportFragmentManager)
                        }

                        is UiState.Failure -> {
                            loadingDialogFragment.hideProgressDialog()
                            state.throwable.message?.let { showSnackBarMessage(it) }
                        }
                    }
                }
            }
        }
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

}


@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.mipmap.app_logo),
        contentDescription = stringResource(R.string.dailyfilm_logo),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        alignment = Alignment.Center
    )

}

@Composable
fun LoginButton(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    client: GoogleSignInClient,
    backgroundColor: Color,
    contentColor: Color
) {
    Button(
        onClick = {
            activityResultLauncher.launch(client.signInIntent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = BorderStroke(2.dp, Color(0xC1494747)),
        elevation = ButtonDefaults.buttonElevation()
    ) {
        Image(
            painter = painterResource(id = R.drawable.btn_google_login),
            contentDescription = stringResource(R.string.google_logo)
        )
        Text(text = stringResource(R.string.sign_in_with_google), modifier = Modifier.padding(6.dp))
    }
}