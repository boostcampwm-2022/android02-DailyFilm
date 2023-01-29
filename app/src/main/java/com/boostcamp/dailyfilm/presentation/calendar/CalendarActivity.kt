package com.boostcamp.dailyfilm.presentation.calendar

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.data.DailyFilmDB
import com.boostcamp.dailyfilm.databinding.ActivityCalendarBinding
import com.boostcamp.dailyfilm.databinding.HeaderCalendarDrawerBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateState
import com.boostcamp.dailyfilm.presentation.login.LoginActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.totalfilm.TotalFilmActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

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
        binding.adapter = CalendarPagerAdapter(this)
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
                    R.id.item_play_month -> {
                        if (viewModel.filmFlow.value.isEmpty()) {
                            Snackbar.make(this, NOT_FILM_DATA, Snackbar.LENGTH_SHORT).show()
                            return@setOnMenuItemClickListener true
                        }
                        startActivity(
                            Intent(this@CalendarActivity, TotalFilmActivity::class.java).apply {
                                putParcelableArrayListExtra(
                                    KEY_FILM_ARRAY,
                                    ArrayList(viewModel.filmFlow.value)
                                )
                            }
                        )
                        true
                    }
                    R.id.item_date_picker -> {
                        if (datePickerDialog.isAdded) {
                            return@setOnMenuItemClickListener true
                        }

                        datePickerDialog.show(supportFragmentManager, DATE_PICKER_TAG)
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
                            is CalendarEvent.Logout -> {
                                logout()
                            }
                            is CalendarEvent.DeleteUser->{
                                logout()
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

    private fun navigateToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        finish()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        ).signOut().addOnCompleteListener {
            navigateToLogin()
        }
    }

    companion object {
        const val KEY_DATE_MODEL = "date_model"
        const val KEY_FILM_ARRAY = "film_list"
        private const val MESSAGE_SELECT_DATE = "날짜를 선택해주세요"
        private const val NOT_FILM_DATA = "영상이 존재 하지 않습니다."
        private const val DATE_PICKER_TAG = "date_picker"
    }
}
