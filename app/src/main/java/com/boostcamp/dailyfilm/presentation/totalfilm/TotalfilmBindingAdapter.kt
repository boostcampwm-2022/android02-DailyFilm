package com.boostcamp.dailyfilm.presentation.totalfilm

import androidx.databinding.BindingAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

@BindingAdapter("playTotalVideo")
fun StyledPlayerView.playTotalVideo(filmArray: ArrayList<DateModel>) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0f
            setPlaybackSpeed(2.0f)
        }
    }

    val mediaItems = filmArray.map { dateModel ->
        MediaItem.fromUri(dateModel.videoUrl ?: "")
    }

    player?.apply {
        setMediaItems(mediaItems)
        prepare()
        playWhenReady = true
    }
}
