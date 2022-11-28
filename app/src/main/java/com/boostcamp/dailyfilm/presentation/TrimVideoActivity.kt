package com.boostcamp.dailyfilm.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityTrimViedoBinding
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.gowtham.library.utils.CompressOption
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import com.gowtham.library.utils.TrimmerUtils
import dagger.hilt.android.AndroidEntryPoint

const val HD_WIDTH = 1280

@AndroidEntryPoint
class TrimVideoActivity : BaseActivity<ActivityTrimViedoBinding>(R.layout.activity_trim_viedo) {
    private val viewModel: TimeVideoViewModel by viewModels()

    override fun initView() {
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri: Uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
                    val uriString = Uri.parse("file://$uri")

                    startActivity(
                        Intent(this, UploadFilmActivity::class.java).apply {
                            putExtra(
                                SelectVideoActivity.DATE_VIDEO_ITEM,
                                DateAndVideoModel(uriString, viewModel.infoItem!!.uploadDate)
                            )
                        }
                    )
                    finish()
                } else
                // 오류
                    finish()
            }

        openTrimActivity(startForResult)
    }

    private fun openTrimActivity(activityResultLauncher: ActivityResultLauncher<Intent>) {
        viewModel.infoItem?.let {
            val videoWidthAndHeight = TrimmerUtils.getVideoWidthHeight(this, it.uri)
            val videoWidth = videoWidthAndHeight.first()
            val videoHeight = videoWidthAndHeight.last()
            val (newWidth, newHeight) = getCompressedWidthAndHeight(videoWidth, videoHeight)

            TrimVideo.activity(it.uri.toString())
                .setTrimType(TrimType.FIXED_DURATION)
                .setFixedDuration(10)
                .setHideSeekBar(true)
                .setCompressOption(CompressOption(24, "5M", newWidth, newHeight)) // 720p, 24 FPS, Bitrate 5M
                .start(this, activityResultLauncher)
        }
    }

    // 긴 변을 기준으로 720p 비율로 조정하기 (720p보다 낮은 화질이면 압축 안 함)
    private fun getCompressedWidthAndHeight(videoWidth: Int, videoHeight: Int): IntArray {
        return if (videoWidth > videoHeight) {
            val newWidth = if (videoWidth > HD_WIDTH) HD_WIDTH else videoWidth
            val newHeight =
                if (videoWidth > HD_WIDTH) (videoHeight / (videoWidth.toDouble() / HD_WIDTH)).toInt() else videoHeight

            intArrayOf(newWidth, newHeight)
        } else {
            val newWidth =
                if (videoHeight > HD_WIDTH) (videoWidth / (videoHeight.toDouble() / HD_WIDTH)).toInt() else videoWidth
            val newHeight = if (videoHeight > HD_WIDTH) HD_WIDTH else videoHeight

            intArrayOf(newWidth, newHeight)
        }
    }

}