package com.boostcamp.dailyfilm.presentation.playfilm

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.util.RoundedBackgroundSpan
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

@BindingAdapter("streamVideo")
fun StyledPlayerView.streamVideo(url: String?) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0f
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    url?.let {
        // Add placeholder
        // TODO 제대로 작동하는지 확인 필요
        val artwork:ImageView = findViewById(com.google.android.exoplayer2.ui.R.id.exo_artwork)
        artwork.visibility = View.VISIBLE
        Glide.with(this)
            .load(it)
            .into(artwork)

        val mediaItem = MediaItem.fromUri(it)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }
}

@BindingAdapter("spannableText")
fun TextView.setSpannedString(textContent: String) {

    text = SpannableString(textContent).apply {
        setSpan(
            RoundedBackgroundSpan(),
            0,
            textContent.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

}

@BindingAdapter("syncViewState")
fun LottieAnimationView.syncViewState(isContentShowed: Boolean) {
    val animator: ValueAnimator =
        when (isContentShowed) {
            false -> {
                ValueAnimator.ofFloat(0.67f, 0.25f).apply {
                    duration = 500
                }
            }
            true -> {
                ValueAnimator.ofFloat(0.25f, 0.67f).apply {
                    duration = 500
                }
            }
        }.apply {
            addUpdateListener {
                progress = it.animatedValue as Float
            }
        }.also {
            it.start()
        }
}

@BindingAdapter("visibilityAnimation")
fun View.startVisibilityAnimation(showFlag: Boolean) {
    when (showFlag) {
        true -> {
            visibility = View.VISIBLE
            this.animate()
                .alpha(0.5f)
                .setDuration(500)
                .setListener(object: AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        alpha = 0.5f
                    }
                })
        }
        false -> {
            this.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(object: AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                    }
                })
        }
    }
}