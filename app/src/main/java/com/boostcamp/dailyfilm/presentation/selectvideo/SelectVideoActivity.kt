package com.boostcamp.dailyfilm.presentation.selectvideo

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivitySelectVideoBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_DATE_MODEL
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_FLAG
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.trimvideo.TrimVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectVideoActivity :
    BaseActivity<ActivitySelectVideoBinding>(R.layout.activity_select_video) {
    private val viewModel: SelectVideoViewModel by viewModels()
    private var permission = getPermission()
    private var requestPermissionLauncher = setRequestPermissionLauncher()
    private val requestSettingLauncher = setRequestSettingLauncher()
    private lateinit var animator: ValueAnimator

    override fun initView() {
        binding.viewModel = viewModel
        checkPermission()
        setObserveUserEvent()
    }

    private fun getPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun setRequestPermissionLauncher(): ActivityResultLauncher<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                getRequestPermissionLauncher()
            }
            else -> {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        viewModel.loadVideo()
                    } else {
                        firstRequestPermissionDialog().show()
                    }
                }
            }
        }
    }

    private fun setRequestSettingLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val isGranted = getPermissionIsGranted()
            if (isGranted) viewModel.loadVideo()
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getRequestPermissionLauncher(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when (isGranted) {
                true -> {
                    viewModel.loadVideo()
                }
                else -> {
                    showPermissionDialog()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionDialog() {
        when (if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED") {
            "DENIED" -> {
                firstRequestPermissionDialog().show()
            }
            "EXPLAINED" -> {
                nonFirstRequestPermissionDialog().show()
            }
        }
    }

    private fun checkPermission() {
        val isGranted = getPermissionIsGranted()
        if (isGranted) viewModel.loadVideo() else requestPermissionLauncher.launch(permission)
    }

    private fun getPermissionIsGranted() = packageManager.checkPermission(
        permission,
        packageName
    ) == PackageManager.PERMISSION_GRANTED

    private fun setObserveUserEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        is SelectVideoEvent.NextButtonResult -> {
                            moveToTrimVideo(event.dateAndVideoModelItem)
                        }
                        is SelectVideoEvent.BackButtonResult -> {
                            finish()
                        }
                        is SelectVideoEvent.ControlSoundResult -> {
                            controlSound(event.result)
                        }
                    }
                }
            }
        }
    }

    private fun controlSound(soundFlag: Boolean) {
        if (soundFlag) {
            binding.playerView.player?.volume = 0.5f
            animator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500)
        } else {
            binding.playerView.player?.volume = 0.0f
            animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500)
        }
        animator.addUpdateListener {
            binding.lottieSelectVideoSoundControl.progress =
                it.animatedValue as Float
        }
        animator.start()
    }

    private fun moveToTrimVideo(item: DateAndVideoModel?) {
        if (item != null) {
            startActivity(
                Intent(this, TrimVideoActivity::class.java).apply {
                    putExtra(DATE_VIDEO_ITEM, item)
                    putExtra(KEY_CALENDAR_INDEX, viewModel.calendarIndex)
                    putExtra(KEY_EDIT_FLAG, viewModel.editFlag)
                    putExtra(KEY_DATE_MODEL, viewModel.dateModel)
                }
            )
            finish()
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                R.string.guide_choice_video,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun firstRequestPermissionDialog(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.guide_required_permission_title)
            .setMessage(R.string.guide_required_permission_message)
            .setCancelable(false)
            .setPositiveButton(R.string.text_re_request) { _, _ ->
                requestPermissionLauncher.launch(permission)
            }
            .setNegativeButton(R.string.text_close) { _, _ ->
            }.create()
        return builder
    }

    private fun nonFirstRequestPermissionDialog(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.guide_required_permission_title)
            .setMessage(R.string.guide_required_permission_message)
            .setCancelable(false)
            .setPositiveButton(R.string.text_change_setting) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                requestSettingLauncher.launch(intent)
            }
            .setNegativeButton(R.string.text_close) { _, _ ->
                Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.guide_accept_permission,
                    Snackbar.LENGTH_SHORT
                ).show()
            }.create()
        return builder
    }

    override fun onDestroy() {
        binding.playerView.player?.release()
        binding.playerView.player = null
        super.onDestroy()
    }

    companion object {
        const val DATE_VIDEO_ITEM = "DateAndVideoModel"
    }
}
