package com.boostcamp.dailyfilm.presentation.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentDateBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DateFragment(val onUploadFilm: (DateModel?) -> Unit) :
    BaseFragment<FragmentDateBinding>(R.layout.fragment_date) {

    private val viewModel: DateViewModel by viewModels()
    private lateinit var adapter: CalendarAdapter

    override fun initView() {
        initAdapter()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateFlow.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = CalendarAdapter(
            viewModel.calendar,
            Glide.with(this),
            { dateModel ->
                onUploadFilm(null)
                startActivity(
                    Intent(requireContext(), PlayFilmActivity::class.java).apply {
                        putExtra(KEY_DATE_MODEL, dateModel)
                    }
                )
            },
            { dateModel ->
                onUploadFilm(dateModel)
            }
        )
        binding.rvCalendar.adapter = adapter
        binding.rvCalendar.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.div_calendar_week)?.let {
                    setDrawable(it)
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetchCalendar()
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        const val KEY_DATE_MODEL = "dateModel"
        fun newInstance(calendar: Calendar, lambda: (DateModel?) -> Unit): DateFragment {
            Log.d("DateFragment", "newInstance: ${calendar.get(Calendar.MONTH) + 1}")
            return DateFragment(lambda).apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
