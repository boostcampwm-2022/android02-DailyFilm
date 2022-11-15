package com.boostcamp.dailyfilm.presentation.util.bindingadapter

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter

@SuppressLint("SetTextI18n")
@BindingAdapter(value = ["year", "month", "dayOfMonth"], requireAll = true)
fun TextView.setDate(year: Int, month: Int, dayOfMonth: Int) {
    text = "$year/${month + 1}/$dayOfMonth"
}
