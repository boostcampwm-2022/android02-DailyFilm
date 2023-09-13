package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.app.Activity
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay
import me.saket.extendedspans.ExtendedSpans
import me.saket.extendedspans.RoundedCornerSpanPainter
import me.saket.extendedspans.SquigglyUnderlineSpanPainter
import me.saket.extendedspans.drawBehind
import me.saket.extendedspans.rememberSquigglyUnderlineAnimator
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import kotlin.time.Duration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFilmScreen(
    viewModel: UploadFilmViewModel,
    modifier: Modifier = Modifier,
) {

    val activity = LocalContext.current as Activity
    val editState by viewModel.editState.collectAsStateWithLifecycle()
    val uploadUiState by viewModel.uploadUiState.collectAsStateWithLifecycle()
    val writingState by viewModel.writingState.collectAsStateWithLifecycle()
    val muteState by viewModel.muteState.collectAsStateWithLifecycle()
    val compressState by viewModel.compressState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        KeyboardVisibilityEvent.setEventListener(activity) { viewModel.updateIsWriting(it) }
    }

    LaunchedEffect(uploadUiState) {
        when (uploadUiState) {
            is UploadUiState.UploadFailed -> {
                val state = uploadUiState as UploadUiState.UploadFailed
                state.throwable.message?.let {
                    snackbarHostState.showSnackbar(it)
                }
            }

            else -> {}
        }
    }

    when (writingState) {
        true -> focusRequester.requestFocus()
        false -> LocalFocusManager.current.clearFocus()
    }

    BackgroundVideoPlayer(
        originUri = viewModel.beforeItem?.uri,
        resultUri = viewModel.infoItem?.uri,
        startTime = viewModel.startTime,
        editState = editState,
        muteState = muteState,
        modifier = Modifier.fillMaxSize()
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            UploadFilmTopArea(
                writingState = writingState,
                muteState = muteState,
                backAction = viewModel::cancelUploadVideo,
                uploadAction = viewModel::uploadVideo,
                muteAction = viewModel::controlSound,
                editTextAction = viewModel::changeIsWriting,
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        },
        content = { innerPadding ->
            UploadFilmMainArea(
                compressVal = compressState,
                focusRequester = focusRequester,
                onTextChanged = viewModel::updateTextContent,
                onKeyboardHide = viewModel::changeIsWriting,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = Color.Transparent)
            )
        },
        modifier = modifier
            .background(color = Color.Transparent)
    )

    // 업로드 로딩 Composition
    if (uploadUiState is UploadUiState.UploadLoading)
        UploadLoadingProgress(
            modifier = Modifier
                .fillMaxSize()
        )

}

@Composable
fun UploadFilmTopArea(
    writingState: Boolean,
    muteState: Boolean,
    backAction: () -> Unit,
    uploadAction: () -> Unit,
    muteAction: () -> Unit,
    editTextAction: () -> Unit,
    modifier: Modifier = Modifier
) {

    val muteComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.lottie_sound))
    val writingComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.lottie_writing))

    Box(modifier = modifier) {
        val muteAnimatable = rememberLottieAnimatable()
        LaunchedEffect(muteState) {
            muteAnimatable.animate(
                muteComposition,
                clipSpec = when (muteState) {
                    true -> LottieClipSpec.Progress(0f, 0.5f)
                    false -> LottieClipSpec.Progress(0.5f, 1.0f)
                },
                speed = 5f
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .wrapContentSize()
                .background(color = Color.Transparent)
        ) {

            // 뒤로 가기 버튼
            TopButton(
                onClick = backAction,
                icon = Icons.Outlined.ArrowBack
            )

            LottieButton(
                composition = muteComposition,
                progress = muteAnimatable.progress,
                onClick = muteAction,
                modifier = Modifier
                    .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
            )
        }

        val writingAnimatable = rememberLottieAnimatable()
        LaunchedEffect(writingState) {
            writingAnimatable.animate(
                writingComposition,
                clipSpec = when (writingState) {
                    true -> LottieClipSpec.Progress(0f, 0.5f)
                    false -> LottieClipSpec.Progress(0.5f, 1f)
                },
                speed = 2f
            )
        }
        LottieButton(
            composition = writingComposition,
            progress = writingAnimatable.progress,
            onClick = editTextAction,
            modifier = Modifier
                .align(Alignment.Center)
        )

        // 업로드 버튼
        TopButton(
            onClick = uploadAction,
            icon = Icons.Outlined.Check,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFilmMainArea(
    compressVal: Int,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit,
    onKeyboardHide: () -> Unit,
    modifier: Modifier
) {

    Box(modifier = modifier) {

        LinearProgressIndicator(
            progress = (compressVal.toFloat() / 240f),
            color = MaterialTheme.colors.primary,
            trackColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        val textContent = remember { mutableStateOf("") }
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

        // 실제로 입력받는 필드 (보이지는 않음)
        TextField(
            value = textContent.value,
            onValueChange = { textValue ->
                textContent.value = textValue
                onTextChanged(textValue)
            },
            textStyle = TextStyle(
                color = Color.Transparent, // 실제로는 보여주지 않기
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
            keyboardActions = KeyboardActions(onDone = { onKeyboardHide() }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = Color.Transparent)
                .focusable(true)
                .focusRequester(focusRequester)
        )

        // 눈에 보이는 텍스트
        Text(
            modifier = Modifier
                .drawBehind(extendedSpans)
                .align(Alignment.Center),
            text =
            buildAnnotatedString {
                textContent.value.split("\n").also { texts ->
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

                        if (i < texts.size)
                            appendLine()
                    }
                }
            },
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            onTextLayout = { result ->
                extendedSpans.onTextLayout(result)
            }
        )

    }

}

@Composable
fun TopButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colors.primaryVariant,
            contentColor = MaterialTheme.colors.surface
        ),
        modifier = modifier
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Composable
fun BackgroundVideoPlayer(
    originUri: Uri?,
    resultUri: Uri?,
    startTime: Long,
    editState: EditState?,
    muteState: Boolean,
    modifier: Modifier = Modifier,
) {
    if (editState == null) return

    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val mediaItem = remember {
        var media = MediaItem.EMPTY
        when (editState) {
            EditState.EDIT_CONTENT -> {
                if (resultUri != null) media = MediaItem.fromUri(resultUri)
            }

            EditState.NEW_UPLOAD, EditState.RE_UPLOAD -> {
                if (originUri != null) {
                    media = MediaItem.fromUri(originUri)
                }
            }
        }
        media
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            volume = 0.5f
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    LaunchedEffect(mediaItem) {
        if (editState == EditState.NEW_UPLOAD || editState == EditState.RE_UPLOAD) {
            if (originUri != null) {
                while (true) {
                    exoPlayer.seekTo(startTime)
                    delay(10_000)
                }
            }
        }
    }

    LaunchedEffect(muteState) {
        when (muteState) {
            true -> exoPlayer.volume = 0.0f
            false -> exoPlayer.volume = 0.5f
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = modifier,
            factory = {
                StyledPlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            })
    ) {
        lifecycleOwner.value.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                when(event) {
                    Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                    Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                    else -> {}
                }
            }
        )

        onDispose { exoPlayer.release() }
    }


}

@Composable
fun LottieButton(
    composition: LottieComposition?,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier
        .background(
            color = MaterialTheme.colors.primaryVariant,
            shape = CircleShape
        )
        .size(40.dp)
        .clip(CircleShape)
        .clickable { onClick() }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center)
                .fillMaxSize()
        )
    }

}

@Composable
fun UploadLoadingProgress(
    modifier: Modifier = Modifier
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colors.primaryVariant)
    ) {

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(100.dp),
            color = MaterialTheme.colors.surface,
        )

    }

}

@Preview
@Composable
fun PreviewLottieButton() {
    LottieButton(
        composition = LottieComposition(),
        progress = 0f,
        onClick = {}
    )
}

@Preview
@Composable
fun PreviewLoadingProgress() {
    UploadLoadingProgress()
}

@Preview
@Composable
fun PreviewUploadFilmTopArea() {
    UploadFilmTopArea(
        writingState = true,
        muteState = true,
        backAction = {},
        uploadAction = {},
        muteAction = {},
        editTextAction = {},
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}

@Preview
@Composable
fun PreviewUploadFilmMainArea() {
    UploadFilmMainArea(
        compressVal = 240,
        focusRequester = FocusRequester(),
        onTextChanged = {},
        onKeyboardHide = {},
        modifier = Modifier
            .fillMaxSize()
    )
}