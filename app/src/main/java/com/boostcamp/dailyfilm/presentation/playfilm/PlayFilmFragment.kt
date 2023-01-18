package com.boostcamp.dailyfilm.presentation.playfilm

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentPlayFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayFilmFragment : BaseFragment<FragmentPlayFilmBinding>(R.layout.fragment_play_film) {

    private val viewModel: PlayFilmViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when(state) {
                        is UiState.Uninitialized -> {}
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                           requireActivity().finish()
                        }
                        is UiState.Failure -> {}
                    }
                }
            }
        }
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
