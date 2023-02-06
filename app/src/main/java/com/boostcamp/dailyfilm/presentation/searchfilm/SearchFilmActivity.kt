package com.boostcamp.dailyfilm.presentation.searchfilm

import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivitySearchFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFilmActivity : BaseActivity<ActivitySearchFilmBinding>(R.layout.activity_search_film) {

    private val viewModel: SearchFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
        binding.fragmentManager = supportFragmentManager

        binding.barSearch.setNavigationOnClickListener { finish() }
    }

    companion object {
        const val TAG_DATE_PICKER = "datePicker"
    }
}
