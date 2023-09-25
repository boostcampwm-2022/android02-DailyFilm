package com.boostcamp.dailyfilm.presentation.playfilm.compose

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentPlayFilmComposeBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_STATE
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivityViewModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmBottomSheetDialog
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmViewModel
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity.Companion.DATE_VIDEO_ITEM
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmComposeActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.boostcamp.dailyfilm.presentation.util.network.NetworkManager
import com.boostcamp.dailyfilm.presentation.util.network.NetworkState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFilmComposeFragment :
    BaseFragment<FragmentPlayFilmComposeBinding>(R.layout.fragment_play_film_compose) {

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

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            viewModel.setNetworkState(NetworkState.AVAILABLE)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            viewModel.setNetworkState(NetworkState.LOST)
        }
    }

    @SuppressLint("ShowToast")
    override fun initView() {
        binding.playFilmCompose.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DailyFilmTheme(requireActivity()) {
                    PlayFilmUI(
                        requireActivity(),
                        viewModel = viewModel,
                        setResultCalendar = { state ->
                            activity?.let {
                                it.setResult(
                                    RESULT_OK, Intent(
                                        it, CalendarActivity::class.java
                                    ).apply {
                                        putExtra(
                                            KEY_CALENDAR_INDEX,
                                            activityViewModel.calendarIndex
                                        )
                                        putExtra(KEY_DATE_MODEL, state.dateModel)
                                    })
                                it.finish()
                            }
                        },
                        dialogEvent = { resId ->
                            when(resId) {
                                R.string.delete -> {
                                    viewModel.setDialog(true)
                                }
                                R.string.re_upload -> {
                                    activity?.let {
                                        it.startActivity(
                                            Intent(
                                                it.applicationContext, SelectVideoActivity::class.java
                                            ).apply {
                                                putExtra(
                                                    KEY_CALENDAR_INDEX,
                                                    activityViewModel.calendarIndex
                                                )
                                                putExtra(KEY_DATE_MODEL, viewModel.dateModel)
                                                putExtra(KEY_EDIT_STATE, EditState.RE_UPLOAD)
                                                putExtra(
                                                    DATE_VIDEO_ITEM,
                                                    DateAndVideoModel(
                                                        viewModel.videoUri.value ?: return@PlayFilmUI,
                                                        viewModel.dateModel.getDate()
                                                    )
                                                )
                                            }
                                        )
                                        it.finish()
                                    }
                                }
                                R.string.edit_text -> {
                                    startForResult.launch(
                                        Intent(activity?.applicationContext, UploadFilmComposeActivity::class.java).apply {
                                            putExtra(KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
                                            putExtra(
                                                DATE_VIDEO_ITEM,
                                                DateAndVideoModel(
                                                    viewModel.videoUri.value ?: return@PlayFilmUI,
                                                    viewModel.dateModel.getDate()
                                                )
                                            )
                                            putExtra(KEY_EDIT_STATE, EditState.EDIT_CONTENT)
                                            putExtra(KEY_DATE_MODEL, viewModel.dateModel)
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
        initBinding()
        initDialog()
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    private fun initDialog() {
        playFilmBottomSheetDialog =
            PlayFilmBottomSheetDialog(viewModel, activityViewModel, startForResult)
    }

    override fun onStart() {
        super.onStart()
        NetworkManager.registerNetworkCallback(networkCallback)
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

    override fun onStop() {
        super.onStop()
        NetworkManager.terminateNetworkCallback(networkCallback)
    }

    override fun onDestroyView() {
        binding.backgroundPlayer.player?.release()
        binding.backgroundPlayer.player = null
        super.onDestroyView()
    }

    companion object {
        const val KEY_DATE_MODEL = "dateModel"
        const val KET_EDIT_TEXT = "editText"
        fun newInstance(dateModel: DateModel) =
            PlayFilmComposeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_DATE_MODEL, dateModel)
                }
            }
    }
}
