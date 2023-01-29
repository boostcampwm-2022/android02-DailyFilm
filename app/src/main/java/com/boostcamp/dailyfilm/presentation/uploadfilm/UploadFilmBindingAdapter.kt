package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.animation.ValueAnimator
import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

@BindingAdapter(value = ["updateAnimation", "inputText", "showKeyboard"], requireAll = false)
fun LottieAnimationView.updateAnimation(
    isWriting: Boolean,
    text: EditText,
    activity: UploadFilmActivity
) {
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

@BindingAdapter(value = ["willBePlayed", "compressProgress"], requireAll = false)
fun StyledPlayerView.playVideoWhenReady(uri: Uri?, progress: Int) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0.5f
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    if (progress == 100){
        uri?.let { videoUri ->
            val mediaItem = MediaItem.fromUri(videoUri)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        }
    }
}