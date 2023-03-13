package com.boostcamp.dailyfilm.presentation.searchfilm

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.presentation.searchfilm.adapter.SearchFilmAdapter
import kotlinx.coroutines.launch

@BindingAdapter("itemList", "viewModel", requireAll = true)
fun RecyclerView.updateAdapter(itemList: List<DailyFilmItem?>, viewModel: SearchFilmViewModel) {
    if (adapter == null) {
        adapter = SearchFilmAdapter { index ->
            viewModel.onClickItem(index)
        }
    }

    findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
        findViewTreeLifecycleOwner()?.repeatOnLifecycle(Lifecycle.State.STARTED) {
            (adapter as SearchFilmAdapter).submitList(itemList)
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("startDate", "endDate", requireAll = true)
fun TextView.setSearchRange(startDate: String?, endDate: String?) {
    if (startDate != null && endDate != null) {
        text = "$startDate  ~  $endDate"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setDate", requireAll = true)
fun TextView.setDate(date: String) {
    text = "${date.substring(0, 4)}년 ${date.substring(4, 6)}월 ${date.substring(6)}일"
}
