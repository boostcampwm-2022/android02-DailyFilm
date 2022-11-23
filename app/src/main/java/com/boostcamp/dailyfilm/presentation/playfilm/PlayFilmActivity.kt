package com.boostcamp.dailyfilm.presentation.playfilm

import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityPlayFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity

class PlayFilmActivity : BaseActivity<ActivityPlayFilmBinding>(R.layout.activity_play_film) {

    private val viewModel: PlayFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
    }

    override fun onDestroy() {
        binding.backgroundPlayer.player?.release()
        binding.backgroundPlayer.player = null
        super.onDestroy()
    }
}