package com.boostcamp.dailyfilm.presentation.calendar.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.databinding.ItemDateBinding
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel

class CalendarAdapter(
    val onImgClick: (DateModel) -> Unit,
    val onDayClick: (DateModel) -> Unit,
) :
    ListAdapter<DateModel, CalendarAdapter.DateModelViewHolder>(DateModelDiffCallback()) {

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
            bind.tvDay.setOnClickListener { onDayClick(item) }
            bind.imgThumbnail.setOnClickListener { onImgClick(item) }
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