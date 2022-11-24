package com.boostcamp.dailyfilm.presentation.calendar.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.databinding.ItemDateBinding
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.bumptech.glide.RequestManager
import java.util.*

class CalendarAdapter(
    currentCalendar: Calendar,
    val glide: RequestManager,
    val onImgClick: (DateModel) -> Unit,
    val onDayClick: (DateModel) -> Unit
) : ListAdapter<DateModel, CalendarAdapter.DateModelViewHolder>(diffUtil) {

    private val todayCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 24)
    }
    private val currentMonth = currentCalendar.get(Calendar.MONTH) + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateModelViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateModelViewHolder(binding, parent.measuredHeight / 6 - 2)
    }

    override fun onBindViewHolder(holder: DateModelViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    inner class DateModelViewHolder(val binding: ItemDateBinding, private val itemHeight: Int) :
        RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: DateModel) {
            itemView.layoutParams.height = itemHeight
            binding.dateModel = item
            binding.glide = glide

            val itemCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, item.year.toInt())
                set(Calendar.MONTH, item.month.toInt() - 1)
                set(Calendar.DAY_OF_MONTH, item.day.toInt())
            }

            if (item.month.toInt() != currentMonth ||
                itemCalendar.timeInMillis > todayCalendar.timeInMillis
            ) {
                binding.tvDay.alpha = ALPHA_DISABLE
                binding.imgThumbnail.alpha = ALPHA_DISABLE
            } else {
                if (item.videoUrl != null) {
                    itemView.setOnClickListener {
                        itemView.apply {
                            isFocusableInTouchMode = true
                            requestFocus()
                            clearFocus()
                            isFocusableInTouchMode = false
                        }
                        onImgClick(item)
                    }
                } else {
                    itemView.setOnClickListener {
                        itemView.apply {
                            isFocusableInTouchMode = true
                            requestFocus()
                            isFocusableInTouchMode = false
                        }
                        onDayClick(item)
                    }
                }
            }
        }
    }

    companion object {
        private const val ALPHA_DISABLE = 0.3f
        private val diffUtil = object : DiffUtil.ItemCallback<DateModel>() {
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
}
