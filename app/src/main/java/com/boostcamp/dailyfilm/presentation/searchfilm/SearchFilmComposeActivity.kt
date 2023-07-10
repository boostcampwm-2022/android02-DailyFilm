package com.boostcamp.dailyfilm.presentation.searchfilm

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class SearchFilmComposeActivity : FragmentActivity() {

    private val viewModel: SearchFilmViewModel by viewModels()

    private val startForResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            /*
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val dateModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(PlayFilmFragment.KEY_DATE_MODEL, DateModel::class.java)
                } else {
                    result.data?.getParcelableExtra(PlayFilmFragment.KEY_DATE_MODEL)
                }
                dateModel ?: return@registerForActivityResult
                val calendarIndex = result.data?.getIntExtra(DateFragment.KEY_CALENDAR_INDEX, -1)
                    ?: return@registerForActivityResult
                viewModel.setVideo(calendarIndex, dateModel)
                reloadItem(calendarIndex, dateModel)
            }
            */
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(viewModel)
            observeEvent()
        }
    }

    private fun observeEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        is SearchEvent.ItemClickEvent -> {
                            startForResult.launch(
                                Intent(this@SearchFilmComposeActivity, PlayFilmActivity::class.java).apply {
                                    putExtra(
                                        DateFragment.KEY_CALENDAR_INDEX,
                                        0,
                                    )
                                    putExtra(
                                        DateFragment.KEY_DATE_MODEL_INDEX,
                                        event.index,
                                    )
                                    putParcelableArrayListExtra(
                                        CalendarActivity.KEY_FILM_ARRAY,
                                        ArrayList(viewModel.itemListFlow.value.map { it?.toDateModel() }),
                                    )
                                },
                            )
                        }

                        is SearchEvent.DatePickerEvent -> {
                            MaterialDatePicker.Builder
                                .dateRangePicker()
                                .apply {
                                    if (viewModel.startAt != null && viewModel.endAt != null) {
                                        setSelection(Pair(viewModel.startAt, viewModel.endAt))
                                    }
                                }
                                .build()
                                .apply {
                                    addOnPositiveButtonClickListener { selection ->
                                        viewModel.searchDateRange(selection.first, selection.second)
                                    }
                                    show(supportFragmentManager, TAG_DATE_PICKER)
                                }
                        }

                        is SearchEvent.FinishEvent -> {
                            this@SearchFilmComposeActivity.finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG_DATE_PICKER = "datePicker"
    }
}
