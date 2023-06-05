package com.boostcamp.dailyfilm.presentation.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginComposeActivity : ComponentActivity() {
    private val viewModel by viewModels<LoginViewModel>()
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
                    showSnackBarMessage(this, getString(R.string.failed_google_login))
                }
            }
        }
        setContent {
            LoginUI(viewModel, client, activityResultLauncher)
        }
    }
}

@Composable
fun LoginUI(
    viewModel: LoginViewModel,
    client: GoogleSignInClient,
    activityResultLauncher: ActivityResultLauncher<Intent>
) {
    val activity = LocalContext.current as Activity

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    LoginView(client, activityResultLauncher)
    ProgressLoading(isLoading = isLoading)
    when (val result = uiState.value.getContentIfNotHandled()) {
        is UiState.Uninitialized -> {
            autoLogin(activity)
        }

        is UiState.Loading -> {
            isLoading = true
        }

        is UiState.Success -> {
            isLoading = false
            activity.startActivity(
                Intent(
                    LocalContext.current,
                    CalendarActivity::class.java
                )
            )
            activity.finish()
        }

        is UiState.Failure -> {
            isLoading = false
            result.throwable.message?.let {
                showSnackBarMessage(activity, it)
            }
        }

        else -> {}
    }
}

@Composable
private fun LoginView(
    client: GoogleSignInClient,
    activityResultLauncher: ActivityResultLauncher<Intent>
) {
    val backgroundColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.light_blue)
    } else {
        Color.White
    }
    val contentsColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        colorResource(id = R.color.dark_gray)
    }
    Column {
        Logo()
        LoginButton(activityResultLauncher, client, backgroundColor, contentsColor)
    }
}

@Composable
fun ProgressLoading(isLoading: Boolean) {
    if (isLoading) {
        Surface(
            color = Color.Black.copy(alpha = 0.2f),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))

        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    Modifier.size(150.dp),
                    color = colorResource(id = R.color.dark_gray),
                    strokeWidth = 8.dp
                )
            }
        }
    }
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
            .height(48.dp)
            .padding(start = 48.dp, end = 48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = BorderStroke(2.dp, colorResource(id = R.color.dark_gray))
    ) {
        Image(
            painter = painterResource(id = R.drawable.btn_google_login),
            contentDescription = stringResource(R.string.google_logo)
        )
        Text(text = stringResource(R.string.sign_in_with_google), modifier = Modifier.padding(6.dp))
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

private fun autoLogin(content: Activity) {
    GoogleSignIn.getLastSignedInAccount(content)?.let {
        content.startActivity(Intent(content, CalendarActivity::class.java))
        content.finish()
    }
}

private fun showSnackBarMessage(context: Activity, message: String) {
    Snackbar.make(context.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
}