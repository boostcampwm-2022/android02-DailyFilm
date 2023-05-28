package com.boostcamp.dailyfilm.presentation.settings.compose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.login.LoginActivity
import com.boostcamp.dailyfilm.presentation.settings.SettingsEvent
import com.boostcamp.dailyfilm.presentation.settings.SettingsViewModel
import com.boostcamp.dailyfilm.presentation.ui.theme.ComposeApplicationTheme
import com.boostcamp.dailyfilm.presentation.util.dialog.CustomDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingComposeActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeApplicationTheme {
                SettingUI(viewModel)
            }
        }
    }
}

@Composable
fun SettingUI(viewModel: SettingsViewModel) {
    val activity = LocalContext.current as Activity
    when (viewModel.settingsEventFlow.collectAsStateWithLifecycle().value) {
        is SettingsEvent.Logout, SettingsEvent.DeleteUser -> Logout(activity)
        is SettingsEvent.Back -> activity.finish()
        is SettingsEvent.Initialized -> SettingView(viewModel = viewModel, activity)
    }
}

@Composable
fun SettingView(viewModel: SettingsViewModel, activity: Activity) {
    Scaffold(
        topBar = {
            SettingTopAppBar(activity)
        },
        content = { paddingValues ->
            // paddingValues 처리 ??
            Log.d("Not Solved", "Screen: $paddingValues")

            val logOutDes = stringResource(id = R.string.logOut_description)
            val deleteUserDes = stringResource(id = R.string.delete_user_description)

            DialogUI(viewModel)

            ContentView(
                logOut = {
                    viewModel.openDialog(logOutDes) { viewModel.logout() }
                },
                delete = {
                    viewModel.openDialog(deleteUserDes) { viewModel.deleteUser() }
                })
        })
}

@Composable
fun SettingTopAppBar(activity: Activity) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.setting_toolbar_title),
                color = MaterialTheme.colors.onBackground
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    tint = MaterialTheme.colors.onBackground
                )
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
fun ContentView(logOut: () -> Unit, delete: () -> Unit) {

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SettingColumn {
            SettingTitleTextView(stringResource(id = R.string.account))
            SettingTextView(stringResource(id = R.string.logout), logOut)
            SettingTextView(stringResource(id = R.string.delete), delete)
        }
    }
}

@Composable
fun SettingColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                shape = RoundedCornerShape(8.dp)
            ),
        content = content
    )
}

@Composable
fun SettingTitleTextView(name: String) {
    Text(
        text = name,
        modifier = Modifier.padding(16.dp),
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun SettingTextView(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onClick),
    )
}

@Composable
fun DialogUI(viewModel: SettingsViewModel) {
    val openDialog = viewModel.openDialog.collectAsStateWithLifecycle().value

    if (openDialog.openDialog) {
        CustomDialog(
            text = openDialog.content,
            onDismiss = {
                viewModel.closeDialog()
            }, confirm = openDialog.execution
        )
    }
}


@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PreviewSettingView() {
    ComposeApplicationTheme {
        ContentView({}, {})
    }
}

@Composable
fun Logout(activity: Activity) {

    val context = LocalContext.current

    FirebaseAuth.getInstance().signOut()

    GoogleSignIn.getClient(
        activity, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    ).signOut().addOnCompleteListener {
        navigateToLogin(context)
    }
}

fun navigateToLogin(context: Context) {
    context.startActivity(
        Intent(
            context,
            LoginActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    )
}