package com.boostcamp.dailyfilm.presentation.settings.compose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.boostcamp.dailyfilm.presentation.login.LoginActivity
import com.boostcamp.dailyfilm.presentation.settings.SettingsEvent
import com.boostcamp.dailyfilm.presentation.settings.SettingsViewModel
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
            Screen(viewModel)
        }
    }
}

@Composable
fun Screen(viewModel: SettingsViewModel) {
    val state = viewModel.settingsEventFlow.collectAsState().value
    val activity = LocalContext.current as Activity

    SettingView(viewModel)

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
fun SettingView(viewModel: SettingsViewModel) {

    var logOutDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    SettingView(
        logOut = { logOutDialog = true },
        exit = { deleteDialog = true })

    if (logOutDialog) {
        SettingDialog(text = "정말 로그아웃 하시겠습니까?",
            onDismiss = {
                logOutDialog = false
            }, confirm = {
                viewModel.logout()
            })
    }

    if (deleteDialog) {
        SettingDialog(text = "정말 탈퇴하시겠습니까?",
            onDismiss = {
                deleteDialog = false
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
                text = "계정",
                modifier = Modifier.padding(16.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
            SettingTextView("로그아웃", onClick = logOut)
            SettingTextView("탈퇴", onClick = exit)
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
                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
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
        color = MaterialTheme.colors.onPrimary
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PreviewSettingView() {
    SettingView(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(1f), {}, {}
    )
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
                text = text
            )
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.End)
                    .clickable(onClick = confirm)
                    .padding(12.dp)
            ) {
                Text(
                    text = "취소",
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .clickable(onClick = onDismiss)
                )
                Text(
                    text = "확인",
                    modifier = Modifier
                        .clickable(onClick = confirm)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingDialog() {
    SettingDialog("정말 ??", {}, {})
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