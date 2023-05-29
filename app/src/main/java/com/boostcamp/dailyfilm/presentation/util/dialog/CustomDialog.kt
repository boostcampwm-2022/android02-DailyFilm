package com.boostcamp.dailyfilm.presentation.util.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme

@Composable
fun CustomDialog(text: String, onDismiss: () -> Unit, confirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color.White),
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 24.dp),
                text = text,
                color = Color.Black,
            )
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.End)
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.dismiss),
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .clickable(onClick = onDismiss),
                    color = Color.Black,
                )
                Text(
                    text = stringResource(id = R.string.confirm),
                    modifier = Modifier
                        .clickable(onClick = confirm),
                    color = Color.Black,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingDialog() {
    DailyFilmTheme {
        CustomDialog("PreviewSettingDialog", {}, {})
    }
}
