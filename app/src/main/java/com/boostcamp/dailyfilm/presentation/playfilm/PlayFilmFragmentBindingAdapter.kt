package com.boostcamp.dailyfilm.presentation.playfilm

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.boostcamp.dailyfilm.presentation.util.network.NetworkState
import com.boostcamp.dailyfilm.presentation.util.PlayState
import com.boostcamp.dailyfilm.presentation.util.RoundedBackgroundSpan
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.progressindicator.CircularProgressIndicator

@BindingAdapter(value = ["streamVideo", "viewModel"], requireAll = false)
fun StyledPlayerView.streamVideo(uri: Uri?, viewModel: PlayFilmViewModel) {

    Log.d("StyledPlayerView", "uri: ${uri ?: "null"}")
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0.5f
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    uri?.let {
        val mediaItem = MediaItem.fromUri(it)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    setOnClickListener {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
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
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        alpha = 0.5f
                    }
                })
        }
        false -> {
            this.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                    }
                })
        }
    }
}
@BindingAdapter(value = ["networkViewState", "playState"], requireAll = false)
fun TextView.setVisibility(networkState: NetworkState, playState: PlayState) {
    visibility = if(playState != PlayState.Playing && networkState == NetworkState.LOST) {
        View.VISIBLE
    } else {
        View.GONE
    }
}


@BindingAdapter(value = ["playState"], requireAll = false)
fun CircularProgressIndicator.setVisibility(playState: PlayState) {
    visibility = if (playState != PlayState.Playing) {
        View.VISIBLE
    } else {
        View.GONE
    }
}