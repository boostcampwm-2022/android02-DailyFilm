package com.boostcamp.dailyfilm.presentation.playfilm

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.boostcamp.dailyfilm.presentation.playfilm.adapter.PlayFilmPageAdapter


@BindingAdapter("setAdapter", "setViewModel")
fun ViewPager2.initPlayFilmViewPager(
    playFilmPageAdapter: PlayFilmPageAdapter,
    viewModel: PlayFilmActivityViewModel
) {
    adapter = playFilmPageAdapter
    setCurrentItem(viewModel.dateModelIndex ?: 0, false)
    offscreenPageLimit = 2

}