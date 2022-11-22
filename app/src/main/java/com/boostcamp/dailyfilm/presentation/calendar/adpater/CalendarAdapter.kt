package com.boostcamp.dailyfilm.presentation.calendar.adpater

import android.util.Log
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
) :
    ListAdapter<DateModel, CalendarAdapter.DateModelViewHolder>(DateModelDiffCallback()) {

    private val todayCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 24)
    }
    private val currentMonth = currentCalendar.get(Calendar.MONTH) + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateModelViewHolder {
        Log.d("onCreateViewHolder", "${parent.height}")
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
                binding.tvDay.alpha = 0.3f
                binding.imgThumbnail.alpha = 0.3f
            } else {
                if (item.imgUrl != null) {
                    binding.root.setOnClickListener {
                        binding.root.apply {
                            isFocusableInTouchMode = true
                            requestFocus()
                            clearFocus()
                            isFocusableInTouchMode = false
                        }
                        onImgClick(item)
                    }
                } else {
                    binding.root.setOnClickListener {
                        binding.root.apply {
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
