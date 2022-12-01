package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.animation.ValueAnimator
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView

@BindingAdapter(value = ["updateAnimation", "inputText", "showKeyboard"], requireAll = false)
fun LottieAnimationView.updateAnimation(isWriting: Boolean, text: EditText, activity: UploadFilmActivity) {
    lateinit var animator: ValueAnimator

    when (isWriting) {
        true -> {
            animator = ValueAnimator.ofFloat(0.0f, 0.5f).apply {
                duration = 500
            }
            text.requestFocus()
            activity.showKeyboard()
        }
        false -> {
            animator = ValueAnimator.ofFloat(0.5f, 1.0f).apply {
                duration = 500
            }
            text.clearFocus()
            activity.hideKeyboard()
        }
    }

    animator.addUpdateListener {
        progress = it.animatedValue as Float
    }
    animator.start()
}