package com.boostcamp.dailyfilm.presentation.totalfilm

import android.animation.ValueAnimator
import android.net.Uri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.airbnb.lottie.LottieAnimationView
import com.boostcamp.dailyfilm.presentation.playfilm.model.SpeedState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@BindingAdapter("setViewModel")
fun StyledPlayerView.playTotalVideo(viewModel: TotalFilmViewModel) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0.5f
            setPlaybackSpeed(viewModel.isSpeed.value?.speed ?: SpeedState.FAST_2.speed)
        }
    }

    player?.apply {
        viewModel.viewModelScope.launch {
            viewModel.downloadedVideoUri.collectLatest { uri ->
                uri?.let {
                    if (uri == Uri.EMPTY) {
                        return@collectLatest
                    } else {
                        addMediaItem(MediaItem.fromUri(it))
                        prepare()
                    }
                }
            }
        }

        playWhenReady = true

        setOnClickListener {
            if (isPlaying) {
                pause()
            } else {
                play()
            }
        }

        addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                player?.let { player ->
                    viewModel.filmArray?.get(player.currentMediaItemIndex)?.let { model ->
                        viewModel.setCurrentDateItem(model)
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    viewModel.changeEndState()
                }
            }
        })
    }
}

@BindingAdapter("changeVolume")
fun StyledPlayerView.changeVolume(isMuted: Boolean) {
    player?.volume =
        when (isMuted) {
            true -> {
                0.0f
            }
            false -> {
                0.5f
            }
        }
}

@BindingAdapter("changeSpeed")
fun StyledPlayerView.changeSpeed(speed: SpeedState) {
    player?.setPlaybackSpeed(speed.speed)
}

@BindingAdapter("syncMuteIcon")
fun LottieAnimationView.syncMuteIcon(isMuted: Boolean) {
    val animator: ValueAnimator =
        when (isMuted) {
            true -> {
                ValueAnimator.ofFloat(0.0f, 0.5f).apply {
                    duration = 500
                }
            }
            false -> {
                ValueAnimator.ofFloat(0.5f, 1.0f).apply {
                    duration = 500
                }
            }
        }.apply {
            addUpdateListener {
                progress = it.animatedValue as Float
            }
        }.also {
            it.start()
        }
}
