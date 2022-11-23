package com.boostcamp.dailyfilm.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityTrimViedoBinding
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.gowtham.library.utils.LogMessage
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrimVideoActivity : BaseActivity<ActivityTrimViedoBinding>(R.layout.activity_trim_viedo) {
    private val viewModel: TimeVideoViewModel by viewModels()

    override fun initView() {
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri : Uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
                val uriString = Uri.parse("file://$uri")

                startActivity(
                    Intent(this, UploadFilmActivity::class.java).apply {
                        putExtra(SelectVideoActivity.DATE_VIDEO_ITEM, DateAndVideoModel(uriString,viewModel.infoItem!!.uploadDate))
                    }
                )
                finish()
            }else
                // 오류
                finish()
        }

            TrimVideo.activity(viewModel.infoItem!!.uri.toString())
                .setTrimType(TrimType.FIXED_DURATION)
                .setFixedDuration(10)
                .setHideSeekBar(true)
                .setTitle("비디오 편집 (임시 Toolbar 제목)")
                .start(this,startForResult)

    }
}