package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.boostcamp.dailyfilm.R
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.databinding.ActivityUploadFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UploadFilmActivity : BaseActivity<ActivityUploadFilmBinding>(R.layout.activity_upload_film) {

    private val viewModel: UploadFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel

        initWriteButton()
        uploadFilmResult()
        cancelUploadResult()
    }

    private fun initWriteButton() {
        binding.btnWriteContent.setOnClickListener(WriteClickListener())
        binding.btnWriteContentAlpha.setOnClickListener(WriteClickListener())

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            binding.btnWriteContentAlpha.alpha = it.animatedValue as Float
        }

        animator.also {
            it.duration = 1400
            it.repeatMode = ValueAnimator.REVERSE
            it.repeatCount = -1
        }.start()
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etContent, 0)
    }

    private fun cancelUploadResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cancelUploadResult.collect {
                    if (it) {
                        finish()
                    }
                }
            }
        }
    }

    private fun uploadFilmResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadFilmInfoResult.collect {
                    if (it) {
                        binding.backgroundPlayer.visibility = View.INVISIBLE
                        viewModel.infoItem!!.uri.path?.let { uri -> File(uri).delete() }
                        finish()
                    } else {
                        binding.lottieUploadingLoading.visibility = View.VISIBLE
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

    inner class WriteClickListener: View.OnClickListener {
        override fun onClick(v: View?) {
            binding.etContent.requestFocus()
            showKeyboard()
        }
    }

}
