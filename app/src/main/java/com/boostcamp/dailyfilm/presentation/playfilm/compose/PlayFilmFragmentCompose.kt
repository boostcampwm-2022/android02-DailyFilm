package com.boostcamp.dailyfilm.presentation.playfilm.compose

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivityViewModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmViewModel
import com.boostcamp.dailyfilm.presentation.playfilm.model.BottomSheetModel
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.ui.theme.blackBlur
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.boostcamp.dailyfilm.presentation.util.PlayState
import com.boostcamp.dailyfilm.presentation.util.dialog.CustomDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

val playFilmBottomSheetModelList = listOf(
    BottomSheetModel(R.drawable.ic_delete, R.string.delete),
    BottomSheetModel(R.drawable.ic_re_upload, R.string.re_upload),
    BottomSheetModel(R.drawable.ic_edit_text, R.string.edit_text)
)

@Composable
fun PlayFilmUI(
    activity: Activity,
    startForResult: ActivityResultLauncher<Intent>,
    activityViewModel: PlayFilmActivityViewModel,
    viewModel: PlayFilmViewModel
) {
    val state = viewModel.playState.collectAsStateWithLifecycle().value

    DialogUI(viewModel = viewModel)

    PlayView(state, viewModel) { title ->
        onDialogClick(
            activity,
            title,
            startForResult,
            activityViewModel,
            viewModel
        )
    }

    when (state) {
        is PlayState.Uninitialized -> {}
        is PlayState.Loading -> {}
        is PlayState.Playing -> {}
        is PlayState.Deleted -> setResultCalendar(state, activity, activityViewModel)
        is PlayState.Failure -> FailurePlay(activity, state)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayView(
    state: PlayState,
    viewModel: PlayFilmViewModel,
    menuClick: (Int) -> Unit,
) {
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val isMuted = viewModel.isMuted.collectAsStateWithLifecycle().value
    val isContentShowed = viewModel.isContentShowed.collectAsStateWithLifecycle().value
    val dateModel = viewModel.dateModel

    val soundComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset(stringResource(R.string.lottie_sound))
    )
    val textComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(resId = R.raw.lottie_textstate)
    )
    val soundAnimatable = rememberLottieAnimatable()
    val textAnimatable = rememberLottieAnimatable()

    // LottieAnimation
    LaunchedEffect(isMuted) {
        soundAnimatable.animate(
            composition = soundComposition,
            clipSpec = if (isMuted) {
                LottieClipSpec.Progress(0.0f, 0.5f)
            } else {
                LottieClipSpec.Progress(0.5f, 1.0f)
            },
        )
    }

    LaunchedEffect(isContentShowed) {
        textAnimatable.animate(
            composition = textComposition, clipSpec = if (isContentShowed) {
                LottieClipSpec.Progress(0.67f, 0.25f)
            } else {
                LottieClipSpec.Progress(0.25f, 0.67f)
            }
        )
    }

    // BottomSheetDialog
    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            playFilmBottomSheetModelList.forEach { model ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.Background))
                ) {
                    BottomSheetView(model, menuClick)
                }
            }
        }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(dimensionResource(id = R.dimen.normal_100))
        ) {

            Box(
                modifier = Modifier
                    .background(blackBlur, RoundedCornerShape(4.dp))
                    .align(Alignment.TopStart)
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    .height(dimensionResource(id = R.dimen.normal_175))
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(
                        R.string.date, dateModel.year, dateModel.month, dateModel.day
                    ),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.normal_100))
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .background(blackBlur, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .size(dimensionResource(id = R.dimen.normal_175))
                        .clickable {
                            scope.launch {
                                bottomState.show()
                            }
                        })

                LottieAnimation(
                    composition = soundComposition,
                    progress = soundAnimatable.progress,
                    modifier = Modifier
                        .background(blackBlur, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                        .size(dimensionResource(id = R.dimen.normal_175))
                        .clickable { viewModel.changeMuteState() })
            }

            AnimatedVisibility(
                visible = isContentShowed,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = dateModel.text ?: "테스트",
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            if (state != PlayState.Playing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LottieAnimation(
                composition = textComposition,
                progress = textAnimatable.progress,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(blackBlur, RoundedCornerShape(50.dp))
                    .size(dimensionResource(id = R.dimen.large_200))
                    .padding(4.dp)
                    .clickable { viewModel.changeShowState() })
        }
    }

}

fun onDialogClick(
    activity: Activity,
    resId: Int,
    startForResult: ActivityResultLauncher<Intent>,
    activityViewModel: PlayFilmActivityViewModel,
    viewModel: PlayFilmViewModel
) {

    when (resId) {
        R.string.delete -> {
            viewModel.openDialog()
        }

        R.string.re_upload -> {
            activity.startActivity(
                Intent(
                    activity.applicationContext, SelectVideoActivity::class.java
                ).apply {
                    putExtra(DateFragment.KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
                    putExtra(PlayFilmFragment.KEY_DATE_MODEL, viewModel.dateModel)
                    putExtra(CalendarActivity.KEY_EDIT_STATE, EditState.RE_UPLOAD)
                    putExtra(
                        SelectVideoActivity.DATE_VIDEO_ITEM,
                        DateAndVideoModel(
                            viewModel.videoUri.value ?: return,
                            viewModel.dateModel.getDate()
                        )
                    )
                }
            )
            activity.finish()
        }

        R.string.edit_text -> {
            startForResult.launch(
                Intent(activity.applicationContext, UploadFilmActivity::class.java).apply {
                    putExtra(DateFragment.KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
                    putExtra(
                        SelectVideoActivity.DATE_VIDEO_ITEM,
                        DateAndVideoModel(
                            viewModel.videoUri.value ?: return,
                            viewModel.dateModel.getDate()
                        )
                    )
                    putExtra(CalendarActivity.KEY_EDIT_STATE, EditState.EDIT_CONTENT)
                    putExtra(PlayFilmFragment.KEY_DATE_MODEL, viewModel.dateModel)
                }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 80)
@Composable
fun BottomSheetPreView() {
    BottomSheetView(playFilmBottomSheetModelList[0], {})
}

@Composable
fun BottomSheetView(model: BottomSheetModel, onClick: (Int) -> Unit) {

    Row(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.normal_100))
            .clickable { onClick(model.title) },
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.normal_125)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(dimensionResource(id = R.dimen.large_125)),
            painter = painterResource(id = model.icon),
            contentDescription = stringResource(id = model.title),
            colorFilter = ColorFilter.tint(colorResource(id = R.color.OnBackground))
        )
        Text(
            text = stringResource(id = model.title),
            color = colorResource(id = R.color.OnBackground),
            fontSize = 20.sp
        )
    }
}

@Composable
fun FailurePlay(activity: Activity, state: PlayState.Failure) {
    state.throwable.message?.let {
        Snackbar.make(
            activity.findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT
        )
    }
}

@Composable
fun DialogUI(viewModel: PlayFilmViewModel) {
    val openDialog = viewModel.openDialog.collectAsStateWithLifecycle().value

    // CustomDialog
    if (openDialog) {
        CustomDialog(
            stringResource(id = R.string.delete_dialog),
            { viewModel.closeDialog() },
            { viewModel.deleteVideo() }
        )
    }
}

fun setResultCalendar(
    state: PlayState.Deleted, activity: Activity, activityViewModel: PlayFilmActivityViewModel
) {
    activity.setResult(Activity.RESULT_OK, Intent(
        activity, CalendarActivity::class.java
    ).apply {
        putExtra(DateFragment.KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
        putExtra(PlayFilmComposeFragment.KEY_DATE_MODEL, state.dateModel)
    })
    activity.finish()
}