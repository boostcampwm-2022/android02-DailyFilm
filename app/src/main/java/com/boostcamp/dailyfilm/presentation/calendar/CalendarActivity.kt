package com.boostcamp.dailyfilm.presentation.calendar

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityCalendarBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateState
import com.boostcamp.dailyfilm.presentation.login.LoginActivity
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.searchfilm.SearchFilmActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.settings.SettingsActivity
import com.boostcamp.dailyfilm.presentation.totalfilm.TotalFilmActivity
import com.boostcamp.dailyfilm.presentation.trimvideo.TrimVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

private val MINIMUM_VIDEO_DURATION_MS = 10000 // 최소 촬영 시간을 10초(ms)로 지정

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

    private var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val videoUri: Uri? = result.data!!.data
            try {
                if (getVideoDuration(videoUri) < MINIMUM_VIDEO_DURATION_MS) {
                    Snackbar.make(binding.root, getString(R.string.guide_min_size), Snackbar.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                startActivity(
                    Intent(this, TrimVideoActivity::class.java).apply {
                        putExtra(
                            SelectVideoActivity.DATE_VIDEO_ITEM,
                            DateAndVideoModel(videoUri!!, item.getDate())
                        )
                        putExtra(KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                        putExtra(KEY_EDIT_STATE, EditState.NEW_UPLOAD)
                        putExtra(KEY_DATE_MODEL, item)
                        putExtra(FLAG_FROM_VIEW, "camera")

                    }
                )
            } catch (e: ApiException) {
            }
        }
    }
    private val viewModel: CalendarViewModel by viewModels()
    private val todayCalendar = Calendar.getInstance(Locale.getDefault())
    private val todayYear = todayCalendar.get(Calendar.YEAR)
    private val todayMonth = todayCalendar.get(Calendar.MONTH)
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var cameraOpen: Animation
    private lateinit var galleryOpen: Animation
    private lateinit var cameraClose: Animation
    private lateinit var galleryClose: Animation
    private lateinit var item: DateModel

    override fun initView() {
        initAnim()
        initViewModel()
        initAdapter()
        initMenu()
        collectFlow()
    }

    private fun initAnim() {
        cameraOpen = AnimationUtils.loadAnimation(this, R.anim.anim_camera_open)
        galleryOpen = AnimationUtils.loadAnimation(this, R.anim.anim_gallery_open)
        cameraClose = AnimationUtils.loadAnimation(this, R.anim.camera_gallery_close)
        galleryClose = AnimationUtils.loadAnimation(this, R.anim.anim_gallery_close)
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
    }

    private fun initAdapter() {
        binding.adapter = CalendarPagerAdapter(this)
    }

    private fun initMenu() {
        datePickerDialog = DatePickerDialog(viewModel.calendar) { year, month ->
            val position = (year * 12 + month) - (todayYear * 12 + todayMonth)
            binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION + position
        }

        binding.barCalendar.apply {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_go_today -> {
                        binding.vpCalendar.currentItem = CalendarPagerAdapter.START_POSITION
                        true
                    }
                    R.id.item_play_month -> {
                        if (viewModel.filmFlow.value.isEmpty()) {
                            Snackbar.make(this, context.getString(R.string.guide_not_exisxt_video), Snackbar.LENGTH_SHORT).show()
                            return@setOnMenuItemClickListener true
                        }
                        startActivity(
                            Intent(this@CalendarActivity, TotalFilmActivity::class.java).apply {
                                putParcelableArrayListExtra(
                                    KEY_FILM_ARRAY,
                                    ArrayList(viewModel.filmFlow.value)
                                )
                                putExtra(KEY_SPEED, viewModel.userSpeed.ordinal)
                            }
                        )
                        true
                    }
                    R.id.item_settings -> {
                        startActivity(
                            Intent(this@CalendarActivity, SettingsActivity::class.java)
                        )
                        true
                    }
                    R.id.item_search -> {
                        startActivity(Intent(this@CalendarActivity, SearchFilmActivity::class.java))
                        true
                    }
                    else -> false
                }
            }

            setNavigationOnClickListener {
                if (datePickerDialog.isAdded.not()) {
                    datePickerDialog.show(supportFragmentManager, DATE_PICKER_TAG)
                }
            }
        }
    }

    private fun collectFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.calendarEventFlow.collect { event ->
                        when (event) {
                            is CalendarEvent.NavigateToCamera -> {
                                uploadFilmByCamera(event.dateModel)
                            }
                            is CalendarEvent.NavigateToGallery -> {
                                uploadFilmByGallery(event.dateModel)
                            }
                            is CalendarEvent.UpdateMonth -> {
                                updateMonth(event.month)
                            }
                            is CalendarEvent.UploadClickOpenButton -> {
                                openFloatingButton()
                            }
                            is CalendarEvent.UploadClickCloseButton -> {
                                closeFloatingButton()
                            }
                        }
                    }
                }
                launch {
                    viewModel.isTodayFlow.collect { isToday ->
                        binding.barCalendar.menu.findItem(R.id.item_go_today).apply {
                            when (isToday) {
                                DateState.BEFORE -> {
                                    isVisible = true
                                    setIcon(R.drawable.ic_double_arrow_right)
                                }
                                DateState.TODAY -> {
                                    isVisible = false
                                }
                                DateState.AFTER -> {
                                    isVisible = true
                                    setIcon(R.drawable.ic_double_arrow_left)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun closeFloatingButton() {
        binding.fabCamera.startAnimation(cameraClose)
        binding.fabGallery.startAnimation(galleryClose)
        binding.ivClose.visibility = View.INVISIBLE
    }

    private fun openFloatingButton() {
        binding.fabCamera.startAnimation(cameraOpen)
        binding.fabGallery.startAnimation(galleryOpen)
        binding.ivClose.visibility = View.VISIBLE
    }

    private fun updateMonth(month: String) {
        binding.tvMon.text = month
    }

    private fun uploadFilmByGallery(item: DateModel?) {
        if (item != null) {
            startActivity(
                Intent(this, SelectVideoActivity::class.java).apply {
                    putExtra(KEY_DATE_MODEL, item)
                    putExtra(KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                    putExtra(KEY_EDIT_STATE, EditState.NEW_UPLOAD)
                }
            )
        } else {
            Snackbar.make(binding.root, this.getString(R.string.guide_check_date), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun uploadFilmByCamera(item: DateModel?) {

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY).not()) {
            Snackbar.make(binding.root, this.getString(R.string.guide_camera_error), Snackbar.LENGTH_SHORT).show()
            return
        }

        if (item != null) {
            this.item = item
            dispatchTakeVideoIntent()
        } else {
            Snackbar.make(binding.root, this.getString(R.string.guide_check_date), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        finish()
    }

    private fun dispatchTakeVideoIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        activityResultLauncher.launch(takePictureIntent)
    }

    private fun getVideoDuration(videoUri: Uri?): Long {
        val cursor = contentResolver.query(videoUri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val duration: Long
        val durationColumnIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)
        duration = if (durationColumnIndex != -1) {
            cursor.getLong(durationColumnIndex)
        } else {
            0
        }
        cursor.close()
        return duration
    }

    companion object {
        const val FLAG_FROM_VIEW = "entryView"
        const val KEY_DATE_MODEL = "dateModel"
        const val KEY_FILM_ARRAY = "filmList"
        const val KEY_EDIT_STATE = "editState"
        const val KEY_SPEED = "speed"
        private const val MESSAGE_SELECT_DATE = "날짜를 선택해주세요"
        private const val NOT_FILM_DATA = "영상이 존재 하지 않습니다."
        private const val DATE_PICKER_TAG = "datePicker"
    }
}
