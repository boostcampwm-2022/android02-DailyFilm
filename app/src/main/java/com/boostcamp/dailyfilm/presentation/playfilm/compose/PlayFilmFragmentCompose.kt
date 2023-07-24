package com.boostcamp.dailyfilm.presentation.playfilm.compose

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmViewModel
import com.boostcamp.dailyfilm.presentation.playfilm.base.ContentShowState
import com.boostcamp.dailyfilm.presentation.playfilm.model.BottomSheetModel
import com.boostcamp.dailyfilm.presentation.ui.theme.blackBlur
import com.boostcamp.dailyfilm.presentation.util.PlayState
import com.boostcamp.dailyfilm.presentation.util.dialog.CustomDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import me.saket.extendedspans.ExtendedSpans
import me.saket.extendedspans.RoundedCornerSpanPainter
import me.saket.extendedspans.SquigglyUnderlineSpanPainter
import me.saket.extendedspans.drawBehind
import me.saket.extendedspans.rememberSquigglyUnderlineAnimator
import kotlin.time.Duration

val playFilmBottomSheetModelList = listOf(
    BottomSheetModel(R.drawable.ic_delete, R.string.delete),
    BottomSheetModel(R.drawable.ic_re_upload, R.string.re_upload),
    BottomSheetModel(R.drawable.ic_edit_text, R.string.edit_text)
)

@Composable
fun PlayFilmUI(
    activity: Activity,
    viewModel: PlayFilmViewModel,
    setResultCalendar: (PlayState.Deleted) -> Unit,
    dialogEvent: (Int) -> Unit
) {
    val state = viewModel.playState.collectAsStateWithLifecycle().value

    DialogUI(viewModel = viewModel)
    PlayScreen(state, viewModel, dialogEvent)

    when (state) {
        is PlayState.Uninitialized -> {}
        is PlayState.Loading -> {}
        is PlayState.Playing -> {}
        is PlayState.Deleted -> setResultCalendar(state)
        is PlayState.Failure -> FailurePlay(activity, state)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayScreen(
    state: PlayState,
    viewModel: PlayFilmViewModel,
    dialogEvent: (Int) -> Unit,
) {
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val muteState by viewModel.muteState.collectAsStateWithLifecycle()
    val contentShowState by viewModel.contentShowState.collectAsStateWithLifecycle()
    val dateModel = viewModel.dateModel

    val soundComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset(stringResource(R.string.lottie_sound))
    )
    val textComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(resId = R.raw.lottie_textstate)
    )
    val soundAnimatable = rememberLottieAnimatable()
    val textAnimatable = rememberLottieAnimatable()

    val squigglyAniamtor = rememberSquigglyUnderlineAnimator(duration = Duration.parse("3s"))
    val extendedSpans = remember {
        ExtendedSpans(
            RoundedCornerSpanPainter(
                padding = RoundedCornerSpanPainter.TextPaddingValues(6.sp, 6.sp),
                topMargin = 2.sp,
                bottomMargin = 2.sp
            ),
            SquigglyUnderlineSpanPainter(wavelength = 20.sp, animator = squigglyAniamtor)
        )
    }

    // LottieAnimation
    LaunchedEffect(muteState.state) {
        soundAnimatable.animate(
            composition = soundComposition,
            clipSpec = muteState.clipSpec,
        )
    }

    LaunchedEffect(contentShowState.state) {
        textAnimatable.animate(
            composition = textComposition, clipSpec = contentShowState.clipSpec
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
                    BottomSheetView(model, dialogEvent)
                }
            }
        }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(dimensionResource(id = R.dimen.normal_100))
        ) {
            DateText(dateModel)

            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.normal_100))
            ) {

                MenuImage(onClick = {
                    scope.launch {
                        bottomState.show()
                    }
                })

                SoundAnimation(
                    soundComposition = soundComposition,
                    soundAnimatable = soundAnimatable,
                    onClick = { muteState.updateState() }
                )
            }

            ContentText(
                extendedSpans = extendedSpans,
                contentShowState = contentShowState,
                dateModel = dateModel
            )

            if (state != PlayState.Playing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            TextLottieAnimation(
                textComposition = textComposition,
                textAnimatable = textAnimatable,
                onClick = { contentShowState.updateState() }
            )
        }
    }

}

@Composable
private fun SoundAnimation(
    soundComposition: LottieComposition?,
    soundAnimatable: LottieAnimatable,
    onClick: () -> Unit
) {
    LottieAnimation(
        composition = soundComposition,
        progress = soundAnimatable.progress,
        modifier = Modifier
            .background(blackBlur, RoundedCornerShape(4.dp))
            .padding(4.dp)
            .size(dimensionResource(id = R.dimen.normal_175))
            .clickable(onClick = onClick)
    )
}

@Composable
private fun MenuImage(
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.ic_menu),
        contentDescription = "Menu",
        modifier = Modifier
            .background(blackBlur, RoundedCornerShape(4.dp))
            .padding(4.dp)
            .size(dimensionResource(id = R.dimen.normal_175))
            .clickable(onClick = onClick)
    )
}

@Composable
private fun BoxScope.ContentText(
    extendedSpans: ExtendedSpans,
    contentShowState: ContentShowState,
    dateModel: DateModel
) {
    AnimatedVisibility(
        visible = contentShowState.state,
        modifier = Modifier.align(Alignment.Center),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = buildAnnotatedString {
                (dateModel.text ?: "").split("\n").also { texts ->
                    texts.forEachIndexed { i, text ->
                        append(
                            extendedSpans.extend(
                                AnnotatedString(
                                    text,
                                    spanStyle = SpanStyle(
                                        textDecoration = TextDecoration.Underline,
                                        color = MaterialTheme.colors.surface,
                                        background = MaterialTheme.colors.primary
                                    ),
                                )
                            )
                        )
                        if (i < texts.size) {
                            appendLine()
                        }
                    }
                }
            },
            modifier = Modifier
                .drawBehind(extendedSpans)
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun BoxScope.TextLottieAnimation(
    textComposition: LottieComposition?,
    textAnimatable: LottieAnimatable,
    onClick: () -> Unit
) {
    LottieAnimation(
        composition = textComposition,
        progress = textAnimatable.progress,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(blackBlur, RoundedCornerShape(50.dp))
            .size(dimensionResource(id = R.dimen.large_200))
            .padding(4.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun BoxScope.DateText(dateModel: DateModel) {
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
}

@Preview(showBackground = true, widthDp = 320, heightDp = 80)
@Composable
fun BottomSheetPreView() {
    BottomSheetView(playFilmBottomSheetModelList[0], {})
}

@Composable
private fun BottomSheetView(model: BottomSheetModel, dialogEvent: (Int) -> Unit) {

    Row(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.normal_100))
            .clickable { dialogEvent(model.title) },
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
private fun FailurePlay(activity: Activity, state: PlayState.Failure) {
    state.throwable.message?.let {
        Snackbar.make(
            activity.findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT
        )
    }
}

@Composable
private fun DialogUI(viewModel: PlayFilmViewModel) {
    val openDialog by viewModel.openDialog.collectAsStateWithLifecycle()

    // CustomDialog
    if (openDialog) {
        CustomDialog(
            stringResource(id = R.string.delete_dialog),
            { viewModel.setDialog(false) },
            { viewModel.deleteVideo() }
        )
    }
}