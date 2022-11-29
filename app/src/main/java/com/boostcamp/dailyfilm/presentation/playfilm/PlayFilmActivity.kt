package com.boostcamp.dailyfilm.presentation.playfilm

import android.util.Log
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityPlayFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.playfilm.adapter.PlayFilmPageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFilmActivity : BaseActivity<ActivityPlayFilmBinding>(R.layout.activity_play_film) {

    private val viewModel: PlayFilmActivityViewModel by viewModels()
    private lateinit var playFilmPageAdapter: PlayFilmPageAdapter

    override fun initView() {
        initViewPager()
    }

    private fun initViewPager() {

        Log.d("PlayFilmActivity", "initViewPager: ${viewModel.filmArray}")
        Log.d("PlayFilmActivity", "index: ${viewModel.dateModelIndex}")

        playFilmPageAdapter = PlayFilmPageAdapter(
            viewModel.filmArray ?: arrayListOf(),
            this
        )

        binding.vpPlayer.apply {
            adapter = playFilmPageAdapter
            setCurrentItem(viewModel.dateModelIndex ?: 0, false)
            offscreenPageLimit = 2

            /*registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })*/
        }
    }
}
