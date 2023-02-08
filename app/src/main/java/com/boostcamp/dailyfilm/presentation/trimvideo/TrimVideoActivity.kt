package com.boostcamp.dailyfilm.presentation.trimvideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityTrimViedoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_STATE
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity.Companion.DATE_VIDEO_ITEM
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.gowtham.library.utils.CompressOption
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import com.gowtham.library.utils.TrimmerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TrimVideoActivity : BaseActivity<ActivityTrimViedoBinding>(R.layout.activity_trim_viedo) {
    private val viewModel: TrimVideoViewModel by viewModels()
    private val startForResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val startTime = TrimVideo.getTrimmedStartMilli(result.data)
                val uri: Uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
                val uriString = Uri.parse("file://$uri")
                viewModel.moveToUpload(uriString, startTime)
            } else {
                viewModel.moveToSelectVideo()
            }
        }

    override fun initView() {
        setObserveUserEvent()
        viewModel.initOpenTrimVideo()
    }

    private fun setObserveUserEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collectLatest { event ->
                    when (val result = event.getContentIfNotHandled()) {
                        is TrimVideoEvent.InitOpenTrimVideo -> {
                            initTrimVideo(result)
                        }
                        is TrimVideoEvent.OpenTrimVideoResult -> {
                            openTrimVideo(result)
                        }
                        is TrimVideoEvent.NextButtonResult -> {
                            moveToUpload(result.dateAndVideoModelItem, result.startTime)

                        }
                        is TrimVideoEvent.BackButtonResult -> {
                            moveToSelectVideo(result.dateModel)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun initTrimVideo(event: TrimVideoEvent.InitOpenTrimVideo) {
        viewModel.openTrimActivity(
            startForResult,
            TrimmerUtils.getVideoWidthHeight(this@TrimVideoActivity, event.dateModel.uri)
        )
    }

    private fun openTrimVideo(event: TrimVideoEvent.OpenTrimVideoResult) {
        TrimVideo.activity(event.dateModel.uri.toString())
            .setTrimType(TrimType.FIXED_DURATION)
            .setFixedDuration(10)
            .setHideSeekBar(true)
            .setCompressOption(
                CompressOption(
                    24,
                    "5M",
                    event.newWidth,
                    event.newHeight
                )
            )
            .start(this@TrimVideoActivity, event.startForResult)
    }

    private fun moveToUpload(trimAndVideoModel: DateAndVideoModel, startTime: Long) {
        startActivity(
            Intent(this, UploadFilmActivity::class.java).apply {
                putExtra(DATE_VIDEO_ITEM, trimAndVideoModel)
                putExtra(KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                putExtra(KEY_INFO_ITEM, viewModel.infoItem)
                putExtra(KEY_START_TIME, startTime)
                putExtra(KEY_EDIT_STATE, viewModel.editState)
                putExtra(KEY_DATE_MODEL, viewModel.dateModel)
            }
        )
        finish()
    }

    private fun moveToSelectVideo(dateModel: DateModel) {
        startActivity(
            Intent(this, SelectVideoActivity::class.java).apply {
                putExtra(
                    KEY_DATE_MODEL,
                    dateModel
                )
            }
        )
        finish()
    }

    companion object {
        const val KEY_INFO_ITEM = "beforeItem"
        const val KEY_START_TIME = "start_time"
        const val KEY_DATE_MODEL = "dateModel"
    }
}