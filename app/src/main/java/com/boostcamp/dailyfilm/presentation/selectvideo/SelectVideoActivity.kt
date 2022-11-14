package com.boostcamp.dailyfilm.presentation.selectvideo

import android.Manifest
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.databinding.ActivitySelectVideoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SelectVideoActivity : BaseActivity<ActivitySelectVideoBinding>(R.layout.activity_select_video) {

    private val viewModel: SelectVideoViewModel by viewModels()
    private val mediaAdapter = SelectVideoAdapter()

    override fun initView() {
        binding.viewModel = viewModel
        requestPermission()
        binding.rvMedia.adapter = mediaAdapter

    }

    private fun requestPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted){
                    viewModel.loadVideo().onEach { videoItem ->
                        mediaAdapter.submitData(videoItem)
                    }.launchIn(lifecycleScope)
                }
            }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
        }else{
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}