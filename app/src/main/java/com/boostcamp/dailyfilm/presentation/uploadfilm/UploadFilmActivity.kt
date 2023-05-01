package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityUploadFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KET_EDIT_TEXT
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KEY_DATE_MODEL
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.trimvideo.TrimVideoActivity
import com.boostcamp.dailyfilm.presentation.util.LottieDialogFragment
import com.boostcamp.dailyfilm.presentation.util.network.NetworkManager
import com.boostcamp.dailyfilm.presentation.util.network.NetworkState
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.boostcamp.dailyfilm.presentation.util.network.networkAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File

@AndroidEntryPoint
class UploadFilmActivity : BaseActivity<ActivityUploadFilmBinding>(R.layout.activity_upload_film) {
    private val loadingDialogFragment by lazy { LottieDialogFragment() }
    private val viewModel: UploadFilmViewModel by viewModels()

    override fun initView() {
        initBinding()
        setOnClickListener()
        detectKeyboardState()
        cancelUploadResult()
        setObserveVideoUploadResult()
        soundControl()
    }

    private fun setOnClickListener() {
        binding.ivSelectVideoNext.setOnClickListener {
            if (NetworkManager.checkNetwork() == NetworkState.LOST) {
                MaterialAlertDialogBuilder(this).networkAlertDialog(resources).show()
                return@setOnClickListener
            }
            viewModel.uploadVideo()
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.activity = this
    }

    private fun setObserveVideoUploadResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is UiState.Success -> {
                            loadingDialogFragment.hideProgressDialog()
                            when (viewModel.editState) {
                                EditState.EDIT_CONTENT -> {
                                    setResult(
                                        RESULT_OK,
                                        Intent(
                                            this@UploadFilmActivity,
                                            PlayFilmActivity::class.java
                                        ).apply {
                                            putExtra(KET_EDIT_TEXT, state.item.text)
                                        }
                                    )
                                }
                                else -> {
                                    setResult(
                                        RESULT_OK,
                                        Intent(
                                            this@UploadFilmActivity,
                                            CalendarActivity::class.java
                                        ).apply {
                                            putExtra(KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                                            putExtra(KEY_DATE_MODEL, state.item)
                                        }
                                    )
                                }
                            }
                            finish()
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

    private fun detectKeyboardState(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            detectKeyboardStateGreaterEqualThan30()
        else
            detectKeyboardStateLessThan30()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun detectKeyboardStateGreaterEqualThan30() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            viewModel.updateIsWriting(imeVisible)
            insets
        }
    }

    private fun detectKeyboardStateLessThan30() {
        KeyboardVisibilityEvent.setEventListener(this) { viewModel.updateIsWriting(it) }
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
                        if (viewModel.beforeItem != null) {
                            viewModel.infoItem!!.uri.path?.let { uri -> File(uri).delete() }
                            startActivity(
                                Intent(
                                    this@UploadFilmActivity,
                                    TrimVideoActivity::class.java
                                ).apply {
                                    putExtra(SelectVideoActivity.DATE_VIDEO_ITEM, viewModel.beforeItem)
                                    putExtra(TrimVideoActivity.KEY_DATE_MODEL, viewModel.dateModel)
                                    putExtra(KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                                    putExtra(CalendarActivity.KEY_EDIT_STATE, viewModel.editState)
                                }
                            )
                        }
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

    override fun onResume() {
        super.onResume()
        binding.backgroundPlayer.player?.play()
    }

    override fun onPause() {
        binding.backgroundPlayer.player?.let { player ->
            if (player.isPlaying) {
                player.seekTo(0L)
                player.pause()
            }
        }
        super.onPause()
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
