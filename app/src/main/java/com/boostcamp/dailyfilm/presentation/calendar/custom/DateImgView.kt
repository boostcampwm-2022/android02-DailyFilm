package com.boostcamp.dailyfilm.presentation.calendar.custom

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

class DateImgView constructor(
    context: Context,
    var dateModel: DateModel,
    private val requestManager: RequestManager
) : AppCompatImageView(context) {

    private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    fun setVideoUrl(dateModel: DateModel) {
        this.dateModel = dateModel
        load(dateModel.videoUrl)
    }

    private fun load(imageUrl: String?) {
        imageUrl ?: return
        requestManager.load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .placeholder(R.color.gray)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(this)
    }
}

