package com.boostcamp.dailyfilm.presentation.uploadfilm

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadFilmInfoResult.collect {
                    if (it) {
                        viewModel.infoItem!!.uri.path?.let { uri -> File(uri).delete() }
                        finish()
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
}
