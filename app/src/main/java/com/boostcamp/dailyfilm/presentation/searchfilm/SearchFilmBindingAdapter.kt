package com.boostcamp.dailyfilm.presentation.searchfilm

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.core.util.Pair
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.presentation.searchfilm.adapter.SearchFilmAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch

@BindingAdapter("itemList", requireAll = true)
fun RecyclerView.updateAdapter(itemList: List<DailyFilmItem?>) {
    if (adapter == null) {
        adapter = SearchFilmAdapter()
    }

    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
        findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.STARTED) {
            (adapter as SearchFilmAdapter).submitList(itemList)
        }
    }
}

@BindingAdapter("setFragmentManager", "viewModel", requireAll = true)
fun MaterialToolbar.initMenu(
    fragmentManager: FragmentManager,
    viewModel: SearchFilmViewModel
) {
    setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            R.id.item_search -> {
                true
            }
            R.id.item_datepicker -> {
                val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Date Range")
                    .apply {
                        if (viewModel.startAt != null && viewModel.endAt != null) {
                            setSelection(Pair(viewModel.startAt, viewModel.endAt))
                        }
                    }
                    .build()

                datePicker.apply {
                    addOnPositiveButtonClickListener { selection ->
                        viewModel.searchDateRange(selection.first, selection.second)
                    }
                    show(fragmentManager, SearchFilmActivity.TAG_DATE_PICKER)
                }

                true
            }
            else -> false
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setDate")
fun TextView.setDate(date: String) {
    text = "${date.substring(0, 4)}년 ${date.substring(4, 6)}월 ${date.substring(6)}일"
}
