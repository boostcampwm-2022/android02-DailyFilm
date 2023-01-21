package com.boostcamp.dailyfilm.presentation.playfilm

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

@BindingAdapter("setImg")
fun ImageView.setImg(
    @DrawableRes id: Int
) {
    setImageResource(id)
}