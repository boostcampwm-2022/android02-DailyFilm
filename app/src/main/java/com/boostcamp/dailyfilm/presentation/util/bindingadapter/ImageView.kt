package com.boostcamp.dailyfilm.presentation.util.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.boostcamp.dailyfilm.R
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

@BindingAdapter(value = ["glide", "loadUrl", "cornerRadius"], requireAll = false)
fun ImageView.loadImage(glide: RequestManager, imageUrl: String?, cornerRadius: Int? = null) {
    imageUrl ?: return
    glide.load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .placeholder(R.color.gray)
        .let { builder ->
            if (cornerRadius != null) {
                builder.transform(CenterCrop(), RoundedCorners(cornerRadius))
            } else {
                builder.transform(CenterCrop())
            }
        }
        .into(this)
}
