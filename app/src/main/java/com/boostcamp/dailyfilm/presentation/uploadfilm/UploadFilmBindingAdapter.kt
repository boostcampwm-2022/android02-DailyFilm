package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.animation.ValueAnimator
import android.net.Uri
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.*

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

@BindingAdapter(value = ["originVideo", "resultVideo", "videoStartTime", "editState"], requireAll = false)
fun StyledPlayerView.playVideoAt(origin: Uri?, result: Uri?, startTime: Long, editState: EditState) {
    if (player == null) {
        player = ExoPlayer.Builder(context).build().apply {
            volume = 0.5f
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    lateinit var mediaItem: MediaItem
    when(editState){
        EditState.EDIT_CONTENT -> { // 내용 수정 모드 -> 로컬 저장 비디오로 미리보기
            result?.let {
                mediaItem = MediaItem.fromUri(it)
            }
        }
        EditState.NEW_UPLOAD, EditState.RE_UPLOAD -> { // 업로드 혹은 재업로드 -> 원본 영상 + 구간 데이터로 미리보기
            origin?.let {
                mediaItem = MediaItem.fromUri(it)

                CoroutineScope(Dispatchers.Main).launch {
                    while (true){
                        player?.seekTo(startTime) // 다시 시작지점으로
                        delay(10000) // 10초 만큼 진행하고
                        if (player == null) // 메모리 누수 방지
                            break
                    }
                }
            }
        }
    }

    player?.setMediaItem(mediaItem)
    player?.prepare()
    player?.play()
}