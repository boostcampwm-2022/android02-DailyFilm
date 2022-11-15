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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@BindingAdapter("updateAdapter")
fun RecyclerView.updateAdapter(videosState: StateFlow<Result<*>>?) {
    if (this.adapter == null) {
        this.adapter = SelectVideoAdapter()
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