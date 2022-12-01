package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.boostcamp.dailyfilm.R
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
        binding.activity = this

        detectKeyboardState()
        uploadFilmResult()
        cancelUploadResult()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun detectKeyboardState() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.etContent) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            viewModel.updateIsWriting(imeVisible)
            insets
        }
    }

    fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etContent, 0)
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etContent.windowToken, 0)
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

    inner class WriteClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            binding.etContent.requestFocus()
            showKeyboard()
        }
    }

}
