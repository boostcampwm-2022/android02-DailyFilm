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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.login.LoginActivity
import com.boostcamp.dailyfilm.presentation.settings.SettingsEvent
import com.boostcamp.dailyfilm.presentation.settings.SettingsViewModel
import com.boostcamp.dailyfilm.presentation.ui.theme.ComposeApplicationTheme
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
                Screen(viewModel)
            }
        }
    }
}

@Composable
fun Screen(viewModel: SettingsViewModel) {
    val activity = LocalContext.current as Activity
    val state = viewModel.settingsEventFlow.collectAsStateWithLifecycle().value
    val logOutDialog = viewModel.logOutDialog.collectAsStateWithLifecycle().value
    val deleteDialog = viewModel.deleteDialog.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "설정 화면", color = MaterialTheme.colors.onBackground)
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
        },
        content = { paddingValues ->
            Log.d("esfse", "Screen: $paddingValues")
            SettingView(viewModel, logOutDialog, deleteDialog)
        })

    when (state) {
        is SettingsEvent.Logout, SettingsEvent.DeleteUser -> {
            Logout(activity)
        }

        is SettingsEvent.Back -> {
            activity.finish()
        }

        is SettingsEvent.Initialized -> {}
    }
}

@Composable
fun SettingView(viewModel: SettingsViewModel, logOutDialog: Boolean, deleteDialog: Boolean) {

    SettingView(
        logOut = { viewModel.openLogOutDialog() },
        exit = { viewModel.openDeleteDialog() })

    if (logOutDialog) {
        SettingDialog(text = stringResource(id = R.string.logOut_description),
            onDismiss = {
                viewModel.closeLogOutDialog()
            }, confirm = {
                viewModel.logout()
            })
    }

    if (deleteDialog) {
        SettingDialog(text = stringResource(id = R.string.delete_user_description),
            onDismiss = {
                viewModel.closeDeleteDialog()
            }, confirm = {
                viewModel.deleteUser()
            })
    }
}

@Composable
fun SettingView(modifier: Modifier = Modifier, logOut: () -> Unit, exit: () -> Unit) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SettingColumn {
            Text(
                text = stringResource(id = R.string.account),
                modifier = Modifier.padding(16.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            SettingTextView(stringResource(id = R.string.logout), onClick = logOut)
            SettingTextView(stringResource(id = R.string.deleteAccount), onClick = exit)
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
fun SettingTextView(name: String, onClick: () -> Unit) {

    Text(
        text = name,
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onClick),
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PreviewSettingView() {
    ComposeApplicationTheme {
        SettingView(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(1f), {}, {}
        )
    }
}

@Composable
fun SettingDialog(text: String, onDismiss: () -> Unit, confirm: () -> Unit) {

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color.White)
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 24.dp),
                text = text,
                color = Color.Black
            )
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.End)
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.dismiss),
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .clickable(onClick = onDismiss),
                    color = Color.Black
                )
                Text(
                    text = stringResource(id = R.string.confirm),
                    modifier = Modifier
                        .clickable(onClick = confirm),
                    color = Color.Black
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingDialog() {
    ComposeApplicationTheme {
        SettingDialog("PreviewSettingDialog", {}, {})
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