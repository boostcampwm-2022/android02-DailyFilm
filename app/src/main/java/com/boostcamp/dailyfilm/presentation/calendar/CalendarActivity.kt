package com.boostcamp.dailyfilm.presentation.calendar

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityCalendarBinding
import com.boostcamp.dailyfilm.databinding.HeaderCalendarDrawerBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateState
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

    private lateinit var calendarPagerAdapter: CalendarPagerAdapter
    private val viewModel: CalendarViewModel by viewModels()
    private val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build().apply {
            addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = it
                }
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)

                val todayCalendar = Calendar.getInstance(Locale.getDefault())
                val todayYear = todayCalendar.get(Calendar.YEAR)
                val todayMonth = todayCalendar.get(Calendar.MONTH)

                val position = (year * 12 + month) - (todayYear * 12 + todayMonth)

                binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION + position
            }

            addOnNegativeButtonClickListener {
                dismiss()
            }
        }

    override fun initView() {
        binding.viewModel = viewModel

        val headerBinding: HeaderCalendarDrawerBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.header_calendar_drawer,
                binding.drawerNavigationView,
                true
            )
        headerBinding.viewModel = viewModel

        calendarPagerAdapter = CalendarPagerAdapter(this) {
            viewModel.changeSelectedItem(it)
        }

        binding.vpCalendar.apply {
            adapter = calendarPagerAdapter
            setCurrentItem(CalendarPagerAdapter.START_POSITION, false)
            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.getViewPagerPosition(position)
                    viewModel.changeSelectedItem(null)
                }
            })
        }

        binding.barCalendar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_go_today -> {
                    binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION
                    true
                }
                R.id.item_date_picker -> {
                    datePicker.show(supportFragmentManager, "")
                    true
                }
                else -> false
            }
        }

        binding.barCalendar.setNavigationOnClickListener {
            binding.layoutDrawerCalendar.open()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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
                launch {
                    viewModel.isTodayFlow.collect { isToday ->
                        binding.barCalendar.menu.findItem(R.id.item_go_today).apply {
                            when (isToday) {
                                DateState.BEFORE -> {
                                    isVisible = true
                                    setIcon(R.drawable.ic_double_arrow_right)
                                }
                                DateState.TODAY -> {
                                    isVisible = false
                                }
                                DateState.AFTER -> {
                                    isVisible = true
                                    setIcon(R.drawable.ic_double_arrow_left)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateMonth(month: String) {
        binding.tvMon.text = month
    }

    private fun uploadFilm(item: DateModel?) {
        if (item != null) {
            Toast.makeText(this, "uploadFilm $item", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, SelectVideoActivity::class.java).apply {
                    putExtra(KEY_DATE_MODEL, item)
                }
            )
        } else {
            Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val KEY_DATE_MODEL = "date_model"
    }
}
