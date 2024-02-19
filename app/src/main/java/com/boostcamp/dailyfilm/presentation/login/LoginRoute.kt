package com.boostcamp.dailyfilm.presentation.login

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    onShowSnackBar: suspend (String) -> Unit,
    navigateToCalendar: () -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val activity = LocalContext.current as Activity
    val webClientId = stringResource(R.string.default_web_client_id)
    val options = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    val client = remember { GoogleSignIn.getClient(activity, options) }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val signInResultLauncher = rememberSignInResultLauncher(
        requestLogin = { idToken -> viewModel.requestLogin(idToken) },
        onShowSnackbar = onShowSnackBar,
        coroutineScope = coroutineScope
    )

    LaunchedEffect(uiState) {
        snapshotFlow { uiState.value.getContentIfNotHandled() }.collect { result ->
            when (result) {
                is UiState.Uninitialized -> {
                    GoogleSignIn.getLastSignedInAccount(activity)?.let { navigateToCalendar() }
                }

                is UiState.Loading -> {
                    isLoading = true
                }

                is UiState.Success -> {
                    isLoading = false
                    navigateToCalendar()
                }

                is UiState.Failure -> {
                    isLoading = false
                    result.throwable.message?.let {
                        coroutineScope.launch { onShowSnackBar(it) }
                    }
                }

                else -> {}
            }
        }
    }

    LoginScreen(
        modifier = modifier,
        client = client,
        signInResultLauncher = signInResultLauncher
    )
    ProgressLoading(isLoading = isLoading)
}

@Composable
fun rememberSignInResultLauncher(
    requestLogin: (String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    coroutineScope: CoroutineScope,
): ActivityResultLauncher<Intent> {
    val errorMsg = stringResource(R.string.failed_google_login)
    return rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                requestLogin(account.idToken!!)
            } catch (e: ApiException) {
                coroutineScope.launch {
                    onShowSnackbar(errorMsg)
                }
            }
        }
    }
}
