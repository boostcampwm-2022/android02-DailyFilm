package com.boostcamp.dailyfilm.presentation.trimvideo

import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityTrimViedoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.gowtham.library.utils.CompressOption
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import com.gowtham.library.utils.TrimmerUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrimVideoActivity : BaseActivity<ActivityTrimViedoBinding>(R.layout.activity_trim_viedo) {
    private val viewModel: TrimVideoViewModel by viewModels()

    override fun initView() {
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri: Uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
                    val uriString = Uri.parse("file://$uri")
                    moveToUpload(uriString)
                } else {
                    moveToSelectVideo()
                }
            }
        openTrimActivity(startForResult)
    }

    private fun moveToUpload(uriString: Uri) {
        startActivity(
            Intent(this, UploadFilmActivity::class.java).apply {
                putExtra(
                    SelectVideoActivity.DATE_VIDEO_ITEM,
                    DateAndVideoModel(uriString, viewModel.infoItem!!.uploadDate)
                )
                putExtra(KEY_INFO_ITEM, viewModel.infoItem)
            }
        )
        finish()
    }

    private fun moveToSelectVideo() {
        startActivity(
            Intent(this, SelectVideoActivity::class.java).apply {
                putExtra(
                    CalendarActivity.KEY_DATE_MODEL,
                    viewModel.infoItem!!.getDateModel()
                )
            }
        )
        finish()
    }

    private fun openTrimActivity(activityResultLauncher: ActivityResultLauncher<Intent>) {
        viewModel.infoItem?.let {
            val videoWidthAndHeight = TrimmerUtils.getVideoWidthHeight(this, it.uri)
            val videoWidth = videoWidthAndHeight.first()
            val videoHeight = videoWidthAndHeight.last()
            val trimVideoBuilder = TrimVideo.activity(it.uri.toString())
                .setTrimType(TrimType.FIXED_DURATION)
                .setFixedDuration(DURATION)
                .setHideSeekBar(true)

            if (videoWidth >= HD_WIDTH || videoHeight >= HD_WIDTH) {
                // 720p, 24 FPS
                if (getRotationData(it.uri) == DEGREES_90 || getRotationData(it.uri) == DEGREES_270) { //
                    trimVideoBuilder.setCompressOption(CompressOption(FRAME_RATE, BIT_RATE, HD_HEIGHT, HD_WIDTH))
                } else {
                    trimVideoBuilder.setCompressOption(CompressOption(FRAME_RATE, BIT_RATE, HD_WIDTH, HD_HEIGHT))
                }
            }
            trimVideoBuilder.start(this, activityResultLauncher)
        }
    }

    private fun getRotationData(videoPath: Uri): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoPath)
        val metaRotation =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)

        return metaRotation?.toInt() ?: 0
    }

    companion object {
        const val HD_WIDTH = 1280
        const val HD_HEIGHT = 720
        const val DEGREES_90 = 90
        const val DEGREES_270 = 270
        const val FRAME_RATE = 24
        const val DURATION = 10L
        const val BIT_RATE = "5M"
        const val KEY_INFO_ITEM = "beforeItem"
    }
}