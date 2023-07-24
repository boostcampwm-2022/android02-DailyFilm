package com.boostcamp.dailyfilm.presentation.totalfilm

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.boostcamp.dailyfilm.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.boostcamp.dailyfilm.presentation.ui.theme.blackBlur
import com.google.android.exoplayer2.Player

@AndroidEntryPoint
class TotalFilmComposeActivity : ComponentActivity() {
    private val viewModel by viewModels<TotalFilmViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exoPlayer = ExoPlayer.Builder(this).build().apply {
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    viewModel.filmArray?.get(currentMediaItemIndex)?.let { model ->
                        viewModel.setCurrentDateItem(model)
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        viewModel.changeEndState()
                    }
                }
            })
        }
        setContent {
            VideoPlayer(viewModel, exoPlayer)
            PlayerControlView(viewModel)
        }
    }
}

@Composable
fun VideoPlayer(viewModel: TotalFilmViewModel, exoPlayer: ExoPlayer) {
    var videoUrl by rememberSaveable { mutableStateOf<Uri?>(null) }
    val isMuted = viewModel.isMuted.collectAsStateWithLifecycle().value
    val isEnded = viewModel.isEnded.collectAsStateWithLifecycle().value
    val isSpeed =   viewModel.isSpeed.collectAsStateWithLifecycle().value
    if (isEnded) {
        (LocalContext.current as Activity).finish()
    }

    exoPlayer.apply {
        LaunchedEffect(Unit) {
            viewModel.loadVideos()
            viewModel.downloadedVideoUri.collectLatest {
                it?.let { videoURL ->
                    if (videoURL != Uri.EMPTY) {
                        addMediaItem(fromUri(videoURL))
                        prepare()
                        videoUrl = videoURL
                    } else {
                        return@collectLatest
                    }
                }
            }
        }
        setPlaybackSpeed(isSpeed.speed)
        volume = when (isMuted) {
            true -> {
                0.0f
            }

            false -> {
                0.5f
            }
        }
    }
    videoUrl?.let { VideoView(it, exoPlayer) }
}

@Composable
fun VideoView(videoUrl: Uri, exoPlayer: ExoPlayer) {

    exoPlayer.currentMediaItem?.let {
        val context = LocalContext.current
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
        DisposableEffect(
            AndroidView(factory = {
                StyledPlayerView(context).apply {
                    player = exoPlayer
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    useController = false
                }.apply {
                    setOnClickListener {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    }
                }
            })
        ) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        exoPlayer.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        exoPlayer.play()
                    }

                    else -> {}
                }
            }
            val lifecycle = lifecycleOwner.value.lifecycle
            lifecycle.addObserver(observer)

            onDispose {
                exoPlayer.release()
                lifecycle.removeObserver(observer)
            }
        }
    }
}

@Composable
fun PlayerControlView(
    viewModel: TotalFilmViewModel,
) {
    val isMuted = viewModel.isMuted.collectAsStateWithLifecycle().value
    val curSpeed = viewModel.isSpeed.collectAsStateWithLifecycle().value
    val isContentShowed = viewModel.isContentShowed.collectAsStateWithLifecycle().value
    val dateModel = viewModel.currentDateItem.collectAsStateWithLifecycle().value

    val soundComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset(stringResource(R.string.lottie_sound))
    )
    val textComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(resId = R.raw.lottie_textstate)
    )
    val soundAnimatable = rememberLottieAnimatable()

    var checked by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition = textComposition,
        restartOnPlay = false,
        isPlaying = isPlaying,
        speed = if (isContentShowed) 1f else -1f,
        clipSpec = LottieClipSpec.Progress(0.25f, 0.67f)
    )
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
    LaunchedEffect(progress) {
        if (progress == 0.67f) {
            isPlaying = false
            checked = true
        }
        if (progress == 0.25f && !checked) {
            isPlaying = false
            checked = false
        }
    }

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
                    R.string.date, dateModel!!.year, dateModel.month, dateModel.day
                ),
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.normal_100))
        ) {
            Box(
                modifier = Modifier
                    .background(blackBlur, RoundedCornerShape(4.dp))
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    .size(dimensionResource(id = R.dimen.normal_175))
                    .clickable { viewModel.changeSpeedState() }
            ) {
                Image(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_fast),
                    contentDescription = stringResource(R.string.control_speed)
                )
            }
            Box(
                modifier = Modifier
                    .background(blackBlur, RoundedCornerShape(4.dp))
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    .height(dimensionResource(id = R.dimen.normal_175))
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = curSpeed.toString(),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
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
                text = dateModel!!.text ?: "",
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp),
                color = Color.White,
                fontSize = 16.sp
            )
        }

        LottieAnimation(
            composition = textComposition,
            progress = progress,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(blackBlur, RoundedCornerShape(50.dp))
                .size(dimensionResource(id = R.dimen.large_200))
                .padding(4.dp)
                .clickable {
                    isPlaying = true
                    viewModel.changeShowState()
                }
        )
    }
}
