package com.boostcamp.dailyfilm.presentation.totalfilm

import android.animation.ValueAnimator
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

@BindingAdapter("setViewModel")
fun StyledPlayerView.playTotalVideo(viewModel: TotalFilmViewModel) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0.5f
            setPlaybackSpeed(2.0f)
        }
    }

    player?.apply {
        addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                player?.let {
                    viewModel.filmArray?.get(it.currentMediaItemIndex)?.let { model ->
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

        setMediaItems(
            viewModel.filmArray?.map { dateModel ->
                MediaItem.fromUri(dateModel.videoUrl ?: "")
            } ?: emptyList()
        )
        prepare()
        playWhenReady = true

        setOnClickListener {
            if (isPlaying) {
                pause()
            } else {
                play()
            }
        }
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
