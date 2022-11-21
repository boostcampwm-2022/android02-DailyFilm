package com.boostcamp.dailyfilm.presentation.util.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.boostcamp.dailyfilm.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory


@BindingAdapter(value = ["loadUrl", "cornerRadius"], requireAll = false)
fun ImageView.loadImage(imageUrl: String?, cornerRadius: Int? = null) {
    imageUrl ?: return
    val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    Glide.with(context)
        .load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .placeholder(R.color.white)
        .let { builder ->
            if (cornerRadius != null) {
                builder.transform(CenterCrop(), RoundedCorners(cornerRadius))
            } else {
                builder.transform(CenterCrop())
            }
        }
        .into(this)


}
