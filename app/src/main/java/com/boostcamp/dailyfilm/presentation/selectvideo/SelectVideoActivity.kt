package com.boostcamp.dailyfilm.presentation.selectvideo

import android.Manifest
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
    private var requestPermissionLauncher = setRequestPermissionLauncher()
    override fun initView() {
        binding.viewModel = viewModel
        checkPermission()
        nextButtonEvent()
        soundControl()
    }

    private fun setRequestPermissionLauncher(): ActivityResultLauncher<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                when (if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO)) "DENIED" else "EXPLAINED") {
                    "DENIED" -> {
                        val builder = permissionDialog()
                        builder.show()
                    }
                    "EXPLAINED" -> {
                        val builder = lastPermissionDialog()
                        builder.show()
                    }
                }
            }
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when (if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) "DENIED" else "EXPLAINED") {
                        "DENIED" -> {
                            val builder = permissionDialog()
                            builder.show()
                        }
                        "EXPLAINED" -> {
                            val builder = lastPermissionDialog()
                            builder.show()
                        }
                    }
                } else {
                    val builder = permissionDialog()
                    builder.show()
                }
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val check = packageManager.checkPermission(
                Manifest.permission.READ_MEDIA_VIDEO,
                "com.boostcamp.dailyfilm"
            )
            if (check == PackageManager.PERMISSION_GRANTED) {
                viewModel.loadVideo()
            } else {
                requestPermission()
            }
        } else {
            val check = packageManager.checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "com.boostcamp.dailyfilm"
            )
            if (check == PackageManager.PERMISSION_GRANTED) {
                viewModel.loadVideo()
            } else {
                requestPermission()
            }
        }
    }

    private fun soundControl() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickSound.collect { check ->
                    if (check) {
                        binding.playerView.player?.volume = 0.5f
                        val animator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500)
                        animator.addUpdateListener {
                            binding.lottieSelectVideoSoundControl.progress =
                                it.animatedValue as Float
                        }
                        animator.start()

                    } else {
                        binding.playerView.player?.volume = 0.0f
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
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)

        } else {
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

    private fun permissionDialog(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("필수 권한 안내")
            .setMessage("아래와 같은 이유로 권한 허용이 필요합니다.\n동영상 접근 권한 \n-영상등록을 위하여 필요합니다.")
            .setCancelable(false)
            .setPositiveButton("권한재요청") { p0, p1 ->
                requestPermission()
            }
            .setNegativeButton("닫기") { p0, p1 ->
            }.create()
        return builder
    }

    private fun lastPermissionDialog(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("필수 권한 안내")
            .setMessage("아래와 같은 이유로 권한 허용이 필요합니다.\n동영상 접근 권한 \n-영상등록을 위하여 필요합니다.")
            .setCancelable(false)
            .setPositiveButton("설정변경") { p0, p1 ->
                Log.d("권한요청", "권한 재요청 로직")
            }
            .setNegativeButton("닫기") { p0, p1 ->
            }.create()
        return builder
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
