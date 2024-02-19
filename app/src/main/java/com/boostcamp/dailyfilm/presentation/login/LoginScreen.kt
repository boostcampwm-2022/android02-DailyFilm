package com.boostcamp.dailyfilm.presentation.login

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boostcamp.dailyfilm.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun LoginScreen(
    modifier: Modifier,
    client: GoogleSignInClient,
    signInResultLauncher: ActivityResultLauncher<Intent>
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
        LoginButton(signInResultLauncher, client, backgroundColor, contentsColor)
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
