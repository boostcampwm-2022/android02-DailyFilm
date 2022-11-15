package com.boostcamp.dailyfilm.presentation.selectvideo

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.boostcamp.dailyfilm.data.model.Result
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@BindingAdapter("playVideo")
fun StyledPlayerView.playVideo(videoItem: VideoItem?) {
    if (player == null){
        player = ExoPlayer.Builder(context).build()
        useController = false
    }

    videoItem?.let {
        val mediaItem = MediaItem.fromUri(it.uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
}

@BindingAdapter(value = ["setVideoChooseListener", "updateAdapter"], requireAll = true)
fun RecyclerView.updateAdapter(videoClickListener: VideoSelectListener, videosState: StateFlow<Result<*>>?) {
    if (this.adapter == null) {
        this.adapter = SelectVideoAdapter(videoClickListener)
    }

    if (videosState != null) {
        when (videosState.value) {
            is Result.Success<*> -> {
                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    (this@updateAdapter.adapter as SelectVideoAdapter).submitData((videosState.value as Result.Success<*>).data as PagingData<VideoItem>)
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
        Log.d("Binding", it.toString())
    }
}