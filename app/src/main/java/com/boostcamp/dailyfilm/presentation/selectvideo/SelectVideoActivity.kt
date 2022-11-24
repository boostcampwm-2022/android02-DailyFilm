package com.boostcamp.dailyfilm.presentation.selectvideo

import android.Manifest
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
import com.boostcamp.dailyfilm.presentation.TrimVideoActivity
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


    }

    private fun nextButtonEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is SelectVideoEvent.NextButtonResult -> {
                            navigateToUpload(event.dateAndVideoModelItem)
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
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.READ_MEDIA_VIDEO,
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

    private fun navigateToUpload(item: DateAndVideoModel?) {
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
