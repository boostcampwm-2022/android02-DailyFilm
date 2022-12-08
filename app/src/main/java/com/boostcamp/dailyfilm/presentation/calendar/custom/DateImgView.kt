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
    private val requestManager: RequestManager,
    private val staticWidth: Int,
    private val staticHeight: Int
) : AppCompatImageView(context) {

    private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(staticWidth, staticHeight)
    }

    private fun loadingImg() {
        load(requestManager, dateModel.videoUrl)
    }

    fun setVideoUrl(dateModel: DateModel) {
        this.dateModel = dateModel
        loadingImg()
    }

    private fun load(glide: RequestManager, imageUrl: String?) {
        imageUrl ?: return
        glide.load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .placeholder(R.color.gray)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(this)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }
}

