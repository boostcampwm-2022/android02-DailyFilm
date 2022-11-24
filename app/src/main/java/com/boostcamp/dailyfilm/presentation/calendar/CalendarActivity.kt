package com.boostcamp.dailyfilm.presentation.calendar

import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

    private lateinit var calendarPagerAdapter: CalendarPagerAdapter
    private val viewModel: CalendarViewModel by viewModels()

    private val todayCalendar = Calendar.getInstance(Locale.getDefault())
    private val todayYear = todayCalendar.get(Calendar.YEAR)
    private val todayMonth = todayCalendar.get(Calendar.MONTH)
    private lateinit var datePickerDialog: DatePickerDialog

    override fun initView() {
        initViewModel()
        initAdapter()
        initMenu()
        collectFlow()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        val headerBinding: HeaderCalendarDrawerBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.header_calendar_drawer,
                binding.drawerNavigationView,
                true
            )
        headerBinding.viewModel = viewModel
    }

    private fun initAdapter() {
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
    }

    private fun initMenu() {
        datePickerDialog = DatePickerDialog(viewModel.calendar) { year, month ->
            val position = (year * 12 + month) - (todayYear * 12 + todayMonth)
            binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION + position
        }

        binding.barCalendar.apply {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_go_today -> {
                        binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION
                        true
                    }
                    R.id.item_date_picker -> {
                        datePickerDialog.show(supportFragmentManager, "date_picker")
                        true
                    }
                    else -> false
                }
            }

            setNavigationOnClickListener {
                binding.layoutDrawerCalendar.open()
            }
        }
    }

    private fun collectFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.calendarEventFlow.collect { event ->
                        when (event) {
                            is CalendarEvent.UploadSuccess -> {
                                uploadFilm(event.dateModel)
                            }
                            is CalendarEvent.UpdateMonth -> {
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
            startActivity(
                Intent(this, SelectVideoActivity::class.java).apply {
                    putExtra(KEY_DATE_MODEL, item)
                }
            )
        } else {
            Snackbar.make(binding.root, MESSAGE_SELECT_DATE, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val KEY_DATE_MODEL = "date_model"
        private const val MESSAGE_SELECT_DATE = "날짜를 선택해주세요"
    }
}
