package com.boostcamp.dailyfilm.presentation.calendar.adpater

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import java.util.*

class CalendarPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): DateFragment {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.add(Calendar.MONTH, getItemId(position).toInt())
        if (getItemId(position).toInt() != 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        return DateFragment.newInstance(calendar)
    }

    override fun getItemId(position: Int): Long = (position - START_POSITION).toLong()

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }
}
