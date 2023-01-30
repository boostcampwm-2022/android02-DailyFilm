package com.boostcamp.dailyfilm.presentation.totalfilm

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityTotalFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TotalFilmActivity : BaseActivity<ActivityTotalFilmBinding>(R.layout.activity_total_film) {

    private val viewModel: TotalFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
        setObserveVideoEnded()
    }

    private fun setObserveVideoEnded() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEnded.collectLatest { isEnded ->
                    if (isEnded) {
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.backgroundPlayer.player?.let { player ->
            if (player.isPlaying.not()) {
                player.play()
            }
        }
    }

    override fun onStop() {
        binding.backgroundPlayer.player?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        binding.backgroundPlayer.player?.release()
        binding.backgroundPlayer.player = null
        super.onDestroy()
    }
}
