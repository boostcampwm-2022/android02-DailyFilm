package com.boostcamp.dailyfilm.presentation.calendar.custom

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView

class DateTextView constructor(
    context: Context,
    private val staticWidth: Int,
    private val staticHeight: Int,
) : AppCompatTextView(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(staticWidth, staticHeight)
    }
}
