package com.boostcamp.dailyfilm.presentation.totalfilm

import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

@BindingAdapter("playTotalVideo")
fun StyledPlayerView.playTotalVideo(viewModel: TotalFilmViewModel) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0f
            setPlaybackSpeed(2.0f)
        }
    }

    player?.apply {
        setMediaItems(
            viewModel.filmArray?.map { dateModel ->
                MediaItem.fromUri(dateModel.videoUrl ?: "")
            } ?: emptyList()
        )
        prepare()
        playWhenReady = true

        addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                player?.let {
                    viewModel.filmArray?.get(it.currentMediaItemIndex)?.let { model ->
                        viewModel.setCurrentDateItem(model)
                    }
                }
            }
        })
    }
}
