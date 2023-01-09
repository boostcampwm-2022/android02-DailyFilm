package com.boostcamp.dailyfilm.presentation.playfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentPlayFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFilmFragment : BaseFragment<FragmentPlayFilmBinding>(R.layout.fragment_play_film) {

    private val viewModel: PlayFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        binding.backgroundPlayer.player?.play()
    }

    override fun onPause() {
        binding.backgroundPlayer.player?.let { player ->
            if (player.isPlaying) {
                player.seekTo(0L)
                player.pause()
            }
        }
        super.onPause()
    }

    override fun onDestroyView() {
        binding.backgroundPlayer.player?.release()
        binding.backgroundPlayer.player = null
        super.onDestroyView()
    }

    companion object {
        const val KEY_DATE_MODEL = "dateModel"
        fun newInstance(dateModel: DateModel) =
            PlayFilmFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_DATE_MODEL, dateModel)
                }
            }
    }
}
