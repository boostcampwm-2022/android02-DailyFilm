package com.boostcamp.dailyfilm.presentation.calendar

import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityCalendarBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

    private lateinit var calendarPagerAdapter: CalendarPagerAdapter
    private val viewModel: CalendarViewModel by viewModels()

    override fun initView() {

        binding.viewModel = viewModel

        calendarPagerAdapter = CalendarPagerAdapter(this) {
            viewModel.onDateItemClicked(it)
        }

        binding.vpCalendar.apply {
            adapter = calendarPagerAdapter
            setCurrentItem(CalendarPagerAdapter.START_POSITION, false)
            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.getViewPagerPosition(position)
                }
            })
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is Event.UploadSuccess -> {
                            uploadFilm(event.dateModel)
                        }
                        is Event.UpdateMonth -> {
                            updateMonth(event.month)
                        }
                    }
                }
            }
        }

        binding.imgBtnGoToday.setOnClickListener {
            binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION
        }

        binding.imgBtnDatePicker.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.addOnPositiveButtonClickListener {
                Log.d("datePicker", "Positive: $it")

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = it
                }
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)

                val todayCalendar = Calendar.getInstance(Locale.getDefault())
                val todayYear = todayCalendar.get(Calendar.YEAR)
                val todayMonth = todayCalendar.get(Calendar.MONTH)

                val position = (year * 12 + month) - (todayYear * 12 + todayMonth)

                Log.d("datePicker", "initView: $position")

                binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION + position
            }

            datePicker.addOnNegativeButtonClickListener {
                Log.d("datePicker", "Negative: $it")
                datePicker.dismiss()
            }

            datePicker.show(supportFragmentManager, "")
        }
    }

    private fun updateMonth(month: String) {
        binding.tvMon.text = month
    }

    private fun uploadFilm(item: DateModel) {
        // TODO : 업로드 화면으로 이동
        Toast.makeText(this, "uploadFilm $item", Toast.LENGTH_SHORT).show()
    }
}
