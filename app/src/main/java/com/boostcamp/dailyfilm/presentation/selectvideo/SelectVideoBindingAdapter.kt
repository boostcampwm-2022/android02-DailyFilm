package com.boostcamp.dailyfilm.presentation.selectvideo

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow


@BindingAdapter("playVideo")
fun StyledPlayerView.playVideo(videoItem: VideoItem?) {
    if (player == null){
        player = ExoPlayer.Builder(context).build()
    }

    videoItem?.let {
        val mediaItem = MediaItem.fromUri(it.uri)
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
}

@BindingAdapter(value = ["setVideoSelectListener", "updateAdapter"], requireAll = true)
fun RecyclerView.updateAdapter(videoClickListener: VideoSelectListener, videosState: StateFlow<Result<*>>?) {
    if (adapter == null) {
        adapter = SelectVideoAdapter(videoClickListener)
    }

    if (videosState != null) {
        when (videosState.value) {
            is Result.Success<*> -> {
                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    (adapter as SelectVideoAdapter).submitData((videosState.value as Result.Success<*>).data as PagingData<VideoItem>)
                }
            }

            is Result.Empty -> TODO()
            is Result.Error -> TODO()
            is Result.Uninitialized -> TODO()
        }
    }
}

@BindingAdapter("updateThumbnails")
fun ImageView.updateThumbnails(uri: Uri?) {
    uri?.let {
        Glide.with(this)
            .load(it)
            .centerCrop()
            .into(this)
    }
}