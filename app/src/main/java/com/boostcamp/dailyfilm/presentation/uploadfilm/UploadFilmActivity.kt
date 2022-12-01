package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.animation.ValueAnimator
import android.content.Intent
import com.boostcamp.dailyfilm.R
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.databinding.ActivityUploadFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.trimvideo.TrimVideoActivity
import com.boostcamp.dailyfilm.presentation.util.LottieDialogFragment
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UploadFilmActivity : BaseActivity<ActivityUploadFilmBinding>(R.layout.activity_upload_film) {
    private val loadingDialogFragment by lazy { LottieDialogFragment() }
    private val viewModel: UploadFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
        cancelUploadResult()
        setObserveVideoUploadResult()
        soundControl()
    }

    private fun setObserveVideoUploadResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is UiState.Success -> {
                            loadingDialogFragment.hideProgressDialog()
                            moveToCalendar()
                        }
                        is UiState.Loading -> {
                            loadingDialogFragment.showProgressDialog(supportFragmentManager)
                        }
                        is UiState.Failure -> {
                            loadingDialogFragment.hideProgressDialog()
                            state.throwable.message?.let { showSnackBarMessage(it) }
                        }
                        is UiState.Uninitialized -> {

                        }
                    }
                }
            }
        }
    }

    private fun moveToCalendar() {
        startActivity(
            Intent(
                this@UploadFilmActivity,
                CalendarActivity::class.java
            )
        )
        finish()
    }

    private fun cancelUploadResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cancelUploadResult.collect {
                    if (it) {
                        viewModel.infoItem!!.uri.path?.let { uri -> File(uri).delete() }
                        startActivity(
                            Intent(this@UploadFilmActivity, TrimVideoActivity::class.java).apply {
                                putExtra(SelectVideoActivity.DATE_VIDEO_ITEM, viewModel.beforeItem)
                            }
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun soundControl() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickSound.collect { check ->
                    if (check) {
                        binding.backgroundPlayer.player?.volume = 0.5f
                        val animator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500)
                        animator.addUpdateListener {
                            binding.lottieSelectVideoSoundControl.progress =
                                it.animatedValue as Float
                        }
                        animator.start()

                    } else {
                        binding.backgroundPlayer.player?.volume = 0.0f
                        val animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500)
                        animator.addUpdateListener {
                            binding.lottieSelectVideoSoundControl.progress =
                                it.animatedValue as Float
                        }
                        animator.start()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        binding.backgroundPlayer.player?.release()
        binding.backgroundPlayer.player = null
        super.onDestroy()
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

}
