package com.boostcamp.dailyfilm.presentation.calendar

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.databinding.BindingAdapter
import com.boostcamp.dailyfilm.presentation.calendar.custom.CalendarView
import com.bumptech.glide.Glide

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter(
    value = ["setDateFragment", "setActivityViewModel", "setViewModel"]
)
fun CalendarView.initCalendarView(
    fragment: DateFragment,
    activityViewModel: CalendarViewModel,
    viewModel: DateViewModel
) {
    initCalendar(
        Glide.with(fragment),
        viewModel.initialDateList(),
        viewModel.calendar
    )

    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                val x = event.x.toInt() / tmpHorizontal         // child horizontal Index
                val y = event.y.toInt() / tmpVertical           // child vertical Index

                val index = (y * 7 + x) * 2

                if (childCount <= index) return@setOnTouchListener false

                setSelected(index) {
                    activityViewModel.changeSelectedItem(it)
                }
                true
            }
            else -> false
        }
    }
}