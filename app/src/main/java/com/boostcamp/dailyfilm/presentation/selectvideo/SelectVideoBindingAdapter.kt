package com.boostcamp.dailyfilm.presentation.selectvideo

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ConcatAdapter.Config
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.presentation.selectvideo.adapter.SelectVideoAdapter
import com.boostcamp.dailyfilm.presentation.selectvideo.adapter.VideoLoadStateAdapter
import com.boostcamp.dailyfilm.presentation.selectvideo.adapter.VideoSelectListener
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@BindingAdapter(value = ["thisItem", "selectedVideo"], requireAll = false)
fun ConstraintLayout.chooseVideoAndUpdateAlpha(
    videoItem: VideoItem,
    clickListener: VideoSelectListener
) {
    with(clickListener) {
        viewTreeLifecycleScope.launch {
            selectedVideo.collect {
                alpha = if (videoItem.uri == it?.uri)
                    0.5f
                else
                    1.0f
            }
        }

        if (selectedVideo.value == null) {
            alpha = 0.5f
            clickListener.chooseVideo(videoItem)
        }

    }
}

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
    videosState: StateFlow<PagingData<VideoItem>>
) {
    if (adapter == null) {
        itemAnimator = null
        val newAdapter = SelectVideoAdapter(videoClickListener)
        adapter = newAdapter.withLoadStateFooter(
            footer = VideoLoadStateAdapter(newAdapter::retry)
        ).apply {
            Config.Builder().apply {
                setIsolateViewTypes(false)
            }.build()
        }

        this.layoutManager = GridLayoutManager(this.context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when ((adapter as ConcatAdapter).getItemViewType(position)) {
                        1 -> {
                            3
                        }
                        else -> {
                            1
                        }
                    }
                }
            }
        }
    }
    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
        findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.STARTED) {
            ((adapter as ConcatAdapter).adapters[0] as SelectVideoAdapter).submitData(videosState.value)
        }
    }
}

@BindingAdapter("updateThumbnails")
fun ImageView.updateThumbnails(uri: Uri?) {
    uri?.let {
        Glide.with(this)
            .load(it)
            .centerCrop()
            .placeholder(com.boostcamp.dailyfilm.R.color.gray)
            .into(this)
    }
}

