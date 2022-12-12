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


const val HD_WIDTH = 1280
const val HD_HEIGHT = 720

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
                putExtra("beforeItem", viewModel.infoItem)
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

            if (videoWidth >= 1280 || videoHeight >= 1280){
                // 720p, 24 FPS
                if (getRotationData(it.uri) == 90 || getRotationData(it.uri) == 270){ //
                    TrimVideo.activity(it.uri.toString())
                        .setTrimType(TrimType.FIXED_DURATION)
                        .setFixedDuration(10)
                        .setHideSeekBar(true)
                        .setCompressOption(CompressOption(24, "5M", HD_HEIGHT, HD_WIDTH))
                        .start(this, activityResultLauncher)
                }
                else{
                    TrimVideo.activity(it.uri.toString())
                        .setTrimType(TrimType.FIXED_DURATION)
                        .setFixedDuration(10)
                        .setHideSeekBar(true)
                        .setCompressOption(CompressOption(24, "5M", HD_WIDTH, HD_HEIGHT))
                        .start(this, activityResultLauncher)
                }
            }
            else{ // HD 해상도보다 작은 영상
                TrimVideo.activity(it.uri.toString())
                    .setTrimType(TrimType.FIXED_DURATION)
                    .setFixedDuration(10)
                    .setHideSeekBar(true)
                    .start(this, activityResultLauncher)
            }
        }
    }

    private fun getRotationData(videoPath: Uri): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoPath)
        val metaRotation= retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)

        return metaRotation?.toInt() ?: 0
    }
}