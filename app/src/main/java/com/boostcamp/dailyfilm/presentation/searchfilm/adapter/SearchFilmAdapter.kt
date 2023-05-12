package com.boostcamp.dailyfilm.presentation.searchfilm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.databinding.ItemSearchResultBinding
import com.bumptech.glide.Glide

class SearchFilmAdapter(private val onClick: (Int) -> Unit) : ListAdapter<DailyFilmItem, SearchFilmAdapter.SearchFilmViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFilmViewHolder {
        return SearchFilmViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_search_result,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchFilmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchFilmViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onClick(absoluteAdapterPosition)
            }
        }

        fun bind(item: DailyFilmItem) {
            binding.item = item
            binding.requestManager = Glide.with(itemView)
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<DailyFilmItem>() {
            override fun areItemsTheSame(oldItem: DailyFilmItem, newItem: DailyFilmItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DailyFilmItem, newItem: DailyFilmItem): Boolean {
                return oldItem.videoUrl == newItem.videoUrl
            }
        }
    }
}
