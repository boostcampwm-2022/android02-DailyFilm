package com.boostcamp.dailyfilm.presentation.calendar.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.databinding.ItemDateBinding
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import java.util.*

class CalendarAdapter(
    private val currentCalendar: Calendar,
    val onImgClick: (DateModel) -> Unit,
    val onDayClick: (DateModel) -> Unit,
) :
    ListAdapter<DateModel, CalendarAdapter.DateModelViewHolder>(DateModelDiffCallback()) {

    private val todayCalendar = Calendar.getInstance(Locale.getDefault())
    private val currentMonth = currentCalendar.get(Calendar.MONTH) + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateModelViewHolder {
        val bind = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateModelViewHolder(bind)
    }

    override fun onBindViewHolder(holder: DateModelViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    inner class DateModelViewHolder(val bind: ItemDateBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun setItem(item: DateModel) {
            bind.dateModel = item

            val itemCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, item.year.toInt())
                set(Calendar.MONTH, item.month.toInt() - 1)
                set(Calendar.DAY_OF_MONTH, item.day.toInt())
            }

            if (item.month.toInt() != currentMonth ||
                itemCalendar.timeInMillis > todayCalendar.timeInMillis
            ) {
                bind.tvDay.alpha = 0.3f
                bind.imgThumbnail.alpha = 0.3f
            } else {
                if (item.imgUrl != null) {
                    bind.root.setOnClickListener { onImgClick(item) }
                }
                else {
                    bind.root.setOnClickListener {
                        bind.root.isFocusableInTouchMode = true
                        bind.root.requestFocus()
                        bind.root.isFocusableInTouchMode = false
                        onDayClick(item)
                    }
                }
            }
        }
    }

    class DateModelDiffCallback : DiffUtil.ItemCallback<DateModel>() {
        override fun areItemsTheSame(
            oldItem: DateModel,
            newItem: DateModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DateModel,
            newItem: DateModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}