package com.boostcamp.dailyfilm.presentation.calendar.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.bumptech.glide.RequestManager
import java.util.*


class CalendarView(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var tmpHorizontal = 0
        private set
    var tmpVertical = 0
        private set
    private var textHeight = convertSPtoPX() + TEXT_PADDING_TOP * 2

    private var selected: Int? = null
    private var currentMonth: Int? = null

    private lateinit var requestManager: RequestManager

    private val todayCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 24)
    }
    private val paint = Paint().apply {
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

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

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        repeat(WEEK_SIZE - 1) {
            canvas?.drawLine(
                0f,
                ((it + 1) * tmpVertical).toFloat(),
                width.toFloat(),
                ((it + 1) * tmpVertical).toFloat(),
                paint
            )
        }
    }

    fun initCalendar(
        requestManager: RequestManager,
        dateModelList: List<DateModel>,
        currentCalendar: Calendar,
    ) {
        currentMonth = currentCalendar.get(Calendar.MONTH) + 1
        this.requestManager = requestManager

        dateModelList.forEach { dateModel ->

            val dateTextView = createDateTextView(dateModel)
            val dateImgView = createDateImgView(dateModel)

            if (isNotCurrentMonth(dateModel)) {
                dateTextView.apply {
                    alpha = ALPHA_DISABLE
                    setOnClickListener { }
                }
                dateImgView.apply {
                    alpha = ALPHA_DISABLE
                    setOnClickListener { }
                }
            }
            addView(dateTextView)
            addView(dateImgView)
        }
    }

    fun reloadItem(index: Int, dateModel: DateModel, callback: (DateModel) -> (Unit)) {
        val imgIndex = index * 2 + 1
        if (dateModel.videoUrl == null) {
            removeViewAt(imgIndex)
            addView(createDateImgView(dateModel), imgIndex)
            return
        }
        (children.toList()[imgIndex] as DateImgView).apply {
            setVideoUrl(dateModel)
            if (isNotCurrentMonth(dateModel)) return
            setOnClickListener {
                callback(dateModel)
            }
        }
    }

    private fun createDateTextView(dateModel: DateModel) = DateTextView(
        context,
        tmpHorizontal,
        textHeight
    ).apply {
        text = dateModel.day
        setTextColor(ContextCompat.getColor(this.context, R.color.OnSurface))
        textSize = TEXT_SIZE
        gravity = Gravity.CENTER
        setPadding(0, TEXT_PADDING_TOP, 0, 0)
    }

    private fun createDateImgView(dateModel: DateModel) = DateImgView(
        context,
        dateModel,
        requestManager,
        tmpHorizontal,
        tmpVertical - textHeight
    ).apply {
        setPadding(IMG_PADDING)
    }

    fun setSelected(index: Int, changedSelectedItem: (DateModel?) -> Unit) {

        resetBackground()

        val imgView = getChildAt(index + 1) as DateImgView

        if (imgView.dateModel.videoUrl != null) {
            return
        }

        getChildAt(index).setBackgroundResource(R.color.gray)
        imgView.setBackgroundResource(R.color.gray)

        selected = index

        changedSelectedItem(imgView.dateModel)
    }

    private fun isNotCurrentMonth(dateModel: DateModel): Boolean {
        val itemCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, dateModel.year.toInt())
            set(Calendar.MONTH, dateModel.month.toInt() - 1)
            set(Calendar.DAY_OF_MONTH, dateModel.day.toInt())
        }
        return dateModel.month.toInt() != currentMonth ||
                itemCalendar.timeInMillis > todayCalendar.timeInMillis
    }

    fun resetBackground() {
        selected?.let {
            getChildAt(it).setBackgroundResource(android.R.color.transparent)
            getChildAt(it + 1).setBackgroundResource(android.R.color.transparent)
        }
    }

    private fun convertSPtoPX(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            TEXT_SIZE,
            resources.displayMetrics
        ).toInt()
    }

    companion object {
        private const val WEEK_SIZE = 6
        private const val DAY_OF_THE_WEEK_SIZE = 7
        private const val ALPHA_DISABLE = 0.3f
        private const val TEXT_SIZE = 15f
        private const val TEXT_PADDING_TOP = 8
        private const val IMG_PADDING = 10
        private const val STROKE_WIDTH = 5f
    }
}