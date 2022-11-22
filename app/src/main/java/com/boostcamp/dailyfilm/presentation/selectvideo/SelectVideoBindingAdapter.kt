package com.boostcamp.dailyfilm.presentation.selectvideo

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@BindingAdapter("onUploaded")
fun TextView.showResultOnSnackBar(uploadResult: SharedFlow<Boolean>) {
    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
        findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.STARTED) {
            uploadResult.collect {
                if (it)
                    Snackbar.make(this@showResultOnSnackBar, "업로드 성공", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

@BindingAdapter("playVideo")
fun StyledPlayerView.playVideo(uri: Uri?) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0f
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    uri?.let {
        val mediaItem = MediaItem.fromUri(it)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
}

@BindingAdapter(value = ["setVideoSelectListener", "updateAdapter"], requireAll = true)
fun RecyclerView.updateAdapter(
    videoClickListener: VideoSelectListener,
    videosState: StateFlow<Result<*>>?
) {
    if (adapter == null) {
        adapter = SelectVideoAdapter(videoClickListener)
    }

    if (videosState != null) {
        when (videosState.value) {
            is Result.Success<*> -> {
                findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        (adapter as SelectVideoAdapter).submitData((videosState.value as Result.Success<*>).data as PagingData<VideoItem>)
                    }
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

