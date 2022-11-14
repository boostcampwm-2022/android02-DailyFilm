package com.boostcamp.dailyfilm.presentation.selectvideo

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.StateFlow


@BindingAdapter("updateAdapter")
fun RecyclerView.updateAdapter(itemList:StateFlow<PagingData<VideoItem>>?){
 /*   itemList?.let {
        if (this.adapter == null)
            this.adapter = SelectVideoAdapter()

        (this.adapter as SelectVideoAdapter).submitData(it)
    }*/
}

@BindingAdapter("updateThumbnails")
fun ImageView.updateThumbnails(uri: Uri?){
    uri?.let {
        Glide.with(this)
            .load(it)
            .centerCrop()
            .into(this)
        Log.d("Binding", it.toString())
    }
}