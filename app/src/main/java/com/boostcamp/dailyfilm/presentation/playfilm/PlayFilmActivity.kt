package com.boostcamp.dailyfilm.presentation.playfilm

import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityPlayFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.playfilm.adapter.PlayFilmPageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFilmActivity : BaseActivity<ActivityPlayFilmBinding>(R.layout.activity_play_film) {

    private val viewModel: PlayFilmActivityViewModel by viewModels()

    override fun initView() {
        initBinding()
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.adapter = PlayFilmPageAdapter(
            viewModel.filmArray ?: arrayListOf(),
            this
        )
    }
}
