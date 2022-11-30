package com.boostcamp.dailyfilm.presentation.selectvideo

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivitySelectVideoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.trimvideo.TrimVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectVideoActivity :
    BaseActivity<ActivitySelectVideoBinding>(R.layout.activity_select_video) {
    private val viewModel: SelectVideoViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
        requestPermission()
        nextButtonEvent()
        soundControl()
    }
    private fun soundControl(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickSound.collect { check ->
                  if (check){
                      binding.playerView.player?.volume = 0.5f
                      val animator = ValueAnimator.ofFloat(0.5f,1.0f).setDuration(500)
                      animator.addUpdateListener {
                          binding.lottieSelectVideoSoundControl.progress=it.animatedValue as Float
                      }
                      animator.start()

                  }else{
                      binding.playerView.player?.volume = 0.0f
                      val animator = ValueAnimator.ofFloat(0f,0.5f).setDuration(500)
                      animator.addUpdateListener {
                          binding.lottieSelectVideoSoundControl.progress=it.animatedValue as Float
                      }
                      animator.start()
                  }
                }
            }
        }
    }
    private fun nextButtonEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is SelectVideoEvent.NextButtonResult -> {
                            moveToTrimVideo(event.dateAndVideoModelItem)
                        }
                        is SelectVideoEvent.BackButtonResult -> {
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestMultiplePermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    permissions.entries.forEach {
                        if (it.value) {
                            viewModel.loadVideo()
                        }
                    }
                }
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        viewModel.loadVideo()
                    }
                }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun moveToTrimVideo(item: DateAndVideoModel?) {
        if (item != null) {
            startActivity(
                Intent(this, TrimVideoActivity::class.java).apply {
                    putExtra(DATE_VIDEO_ITEM, item)
                }
            )
            finish()
        } else {
            Toast.makeText(this, "비디오를 선택해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        binding.playerView.player?.release()
        binding.playerView.player = null
        super.onDestroy()
    }

    companion object {
        const val DATE_VIDEO_ITEM = "DateAndVideoModel"
    }
}
