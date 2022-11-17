package com.boostcamp.dailyfilm.presentation.selectvideo

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivitySelectVideoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectVideoActivity : BaseActivity<ActivitySelectVideoBinding>(R.layout.activity_select_video) {

    private val viewModel: SelectVideoViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
        requestPermission()

    }

    private fun requestPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted){
                    viewModel.loadVideo()
                }
            }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
        }else{
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}