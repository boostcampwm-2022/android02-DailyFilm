package com.boostcamp.dailyfilm.presentation.trimvideo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityTrimViedoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrimVideoActivity : BaseActivity<ActivityTrimViedoBinding>(R.layout.activity_trim_viedo) {
    private val viewModel: TimeVideoViewModel by viewModels()

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

        TrimVideo.activity(viewModel.infoItem!!.uri.toString())
            .setTrimType(TrimType.FIXED_DURATION)
            .setFixedDuration(10)
            .setHideSeekBar(true)
            .start(this, startForResult)

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
}