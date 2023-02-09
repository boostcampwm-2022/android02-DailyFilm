package com.boostcamp.dailyfilm.presentation.playfilm

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentPlayFilmBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayFilmFragment : BaseFragment<FragmentPlayFilmBinding>(R.layout.fragment_play_film) {

    private val viewModel: PlayFilmViewModel by viewModels()
    private val activityViewModel: PlayFilmActivityViewModel by activityViewModels()
    private lateinit var playFilmBottomSheetDialog: PlayFilmBottomSheetDialog

    private val startForResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val text = result.data?.getStringExtra(KET_EDIT_TEXT) ?: ""
                viewModel.setDateModel(text)
            }
        }

    @SuppressLint("ShowToast")
    override fun initView() {
        binding.viewModel = viewModel
        playFilmBottomSheetDialog =
            PlayFilmBottomSheetDialog(viewModel, activityViewModel, startForResult)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Uninitialized -> {}
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            requireActivity().setResult(RESULT_OK, Intent(
                                requireContext(), CalendarActivity::class.java
                            ).apply {
                                putExtra(KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
                                putExtra(KEY_DATE_MODEL, state.item)
                            })
                            requireActivity().finish()
                        }
                        is UiState.Failure -> {
                            state.throwable.message?.let {
                                Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    it,
                                    Snackbar.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }
        }
        binding.ibMenu.setOnClickListener {
            if (playFilmBottomSheetDialog.isAdded) {
                return@setOnClickListener
            }
            playFilmBottomSheetDialog.show(parentFragmentManager, BOTTOM_SHEET_TAG)
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
        const val BOTTOM_SHEET_TAG = "bottomSheet"
        const val KET_EDIT_TEXT = "editText"
        fun newInstance(dateModel: DateModel) =
            PlayFilmFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_DATE_MODEL, dateModel)
                }
            }
    }
}
