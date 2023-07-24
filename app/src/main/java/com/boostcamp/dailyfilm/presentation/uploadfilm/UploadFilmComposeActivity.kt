package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.trimvideo.TrimVideoActivity
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UploadFilmComposeActivity : ComponentActivity() {

    private val viewModel by viewModels<UploadFilmViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeUiState()

        setContent {
            DailyFilmTheme {
                UploadFilmScreen(viewModel = viewModel)
            }
        }
    }

    private fun subscribeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadUiState.collect { uiState ->
                    when (uiState) {
                        is UploadUiState.Canceled -> {
                            if (viewModel.beforeItem != null) {
                                // 업로드 안 한 영상은 로컬에서도 삭제
                                deleteLocalFile(viewModel.infoItem?.uri?.path)

                                // 돌아가기
                                startActivity(
                                    Intent(
                                        this@UploadFilmComposeActivity,
                                        TrimVideoActivity::class.java
                                    ).apply {
                                        putExtra(CalendarActivity.KEY_EDIT_STATE, viewModel.editState.value)
                                        putExtra(SelectVideoActivity.DATE_VIDEO_ITEM, viewModel.beforeItem)
                                        putExtra(PlayFilmFragment.KEY_DATE_MODEL, viewModel.dateModel)
                                        putExtra(DateFragment.KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                                    }
                                )
                            }
                            finish()
                        }

                        is UploadUiState.UploadSuccess -> {
                            when (viewModel.editState.value) {
                                EditState.EDIT_CONTENT -> {
                                    setResult(
                                        Activity.RESULT_OK,
                                        Intent(
                                            this@UploadFilmComposeActivity,
                                            PlayFilmActivity::class.java
                                        ).apply {
                                            putExtra(PlayFilmFragment.KET_EDIT_TEXT, uiState.dateModel.text)
                                        }
                                    )
                                }
                                else -> {
                                    setResult(
                                        Activity.RESULT_OK,
                                        Intent(
                                            this@UploadFilmComposeActivity,
                                            CalendarActivity::class.java
                                        ).apply {
                                            putExtra(DateFragment.KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                                            putExtra(PlayFilmFragment.KEY_DATE_MODEL, uiState.dateModel)
                                        }
                                    )
                                }
                            }
                            finish()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun deleteLocalFile(filePath: String?) {
        filePath?.let { File(it).delete() }
    }

}