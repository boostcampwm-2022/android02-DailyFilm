package com.boostcamp.dailyfilm.presentation.util.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.boostcamp.dailyfilm.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


@BindingAdapter(value = ["loadUrl", "cornerRadius"], requireAll = false)
fun ImageView.loadImage(imageUrl: String?, cornerRadius: Int? = null) {
    imageUrl ?: return
    Glide.with(context)
        .load(imageUrl)
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
