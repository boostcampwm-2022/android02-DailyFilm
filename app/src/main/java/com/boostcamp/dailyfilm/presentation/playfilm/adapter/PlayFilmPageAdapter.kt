package com.boostcamp.dailyfilm.presentation.playfilm.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment

class PlayFilmPageAdapter(
    private val dateList: ArrayList<DateModel>,
    fragmentActivity: FragmentActivity
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = dateList.size

    override fun createFragment(position: Int): PlayFilmFragment {
        return PlayFilmFragment.newInstance(dateList[position])
    }
}