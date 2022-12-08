package com.boostcamp.dailyfilm.presentation.calendar.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.setPadding
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.bumptech.glide.RequestManager

class CalendarView(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var tmpHorizontal = 0
    private var textHeight = 80
    private var tmpVertical = 0

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        tmpHorizontal = width / DAY_OF_THE_WEEK_SIZE
        tmpVertical = height / WEEK_SIZE

        children.forEachIndexed { index, view ->
            val idx = index / 2

            val verticalIdx = idx / DAY_OF_THE_WEEK_SIZE
            val horizontalIdx = idx % DAY_OF_THE_WEEK_SIZE

            val left = horizontalIdx * tmpHorizontal
            val top = verticalIdx * tmpVertical

            when (index % 2) {
                // text
                0 -> {
                    view.layout(
                        left,
                        verticalIdx * tmpVertical,
                        left + tmpHorizontal,
                        top + textHeight       // text 의 높이 여야 함
                    )
                }
                // img
                1 -> {
                    view.layout(
                        left,
                        top + textHeight,      // text 의 Bottom 이어야 함
                        left + tmpHorizontal,
                        top + tmpVertical
                    )
                }
            }
        }
    }

    fun initCalendar(requestManager: RequestManager, dateModelList: List<DateModel>) {
        dateModelList.forEach { dateModel ->
            addView(
                DateTextView(
                    context,
                    tmpHorizontal,
                    textHeight
                ).apply {
                    text = dateModel.day
                    setTextColor(Color.BLACK)
                    textSize = 15f
                    gravity = Gravity.CENTER
                }
            )
            addView(
                DateImgView(
                    context,
                    dateModel,
                    requestManager,
                    tmpHorizontal,
                    tmpVertical - textHeight
                ).apply {
                    setPadding(10)
                }
            )
        }
    }

    fun reloadItem(index: Int, dateModel: DateModel) {
        (children.toList()[index * 2 + 1] as DateImgView).setVideoUrl(dateModel)
    }

    companion object {
        private const val WEEK_SIZE = 6
        private const val DAY_OF_THE_WEEK_SIZE = 7
    }
}