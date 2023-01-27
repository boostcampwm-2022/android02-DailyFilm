package com.boostcamp.dailyfilm.presentation.playfilm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.DialogBottomSheetBinding
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_FLAG
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KEY_DATE_MODEL
import com.boostcamp.dailyfilm.presentation.playfilm.adapter.PlayFilmBottomSheetAdapter
import com.boostcamp.dailyfilm.presentation.playfilm.model.BottomSheetModel
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity.Companion.DATE_VIDEO_ITEM
import com.boostcamp.dailyfilm.presentation.uploadfilm.UploadFilmActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayFilmBottomSheetDialog(
    val viewModel: PlayFilmViewModel,
    private val activityViewModel: PlayFilmActivityViewModel
) : BottomSheetDialogFragment() {

    private var _binding: DialogBottomSheetBinding? = null
    private val binding get() = _binding ?: error("Binding is null")

    private val adapter = PlayFilmBottomSheetAdapter { resId ->
        when (resId) {
            R.string.delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.delete_dialog))
                    .setNegativeButton(resources.getString(R.string.yes)) { dialog, _ ->
                        viewModel.deleteVideo()
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
            R.string.re_upload -> {
                startActivity(
                    Intent(
                        requireContext(), SelectVideoActivity::class.java
                    ).apply {
                        putExtra(KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
                        putExtra(KEY_DATE_MODEL, viewModel.dateModel)
                        putExtra(KEY_EDIT_FLAG, true)
                        putExtra(
                            DATE_VIDEO_ITEM,
                            DateAndVideoModel(
                                viewModel.videoUri.value ?: return@PlayFilmBottomSheetAdapter,
                                viewModel.dateModel.getDate()
                            )
                        )
                    }
                )
                requireActivity().finish()
            }
            R.string.edit_text -> {
                startActivity(
                    Intent(requireContext(), UploadFilmActivity::class.java).apply {
                        putExtra(KEY_CALENDAR_INDEX, activityViewModel.calendarIndex)
                        putExtra(
                            DATE_VIDEO_ITEM,
                            DateAndVideoModel(
                                viewModel.videoUri.value ?: return@PlayFilmBottomSheetAdapter,
                                viewModel.dateModel.getDate()
                            )
                        )
                        putExtra(KEY_EDIT_FLAG, true)
                        putExtra(KEY_DATE_MODEL, viewModel.dateModel)
                    }
                )
                requireActivity().finish()
            }
        }
    }
    private val bottomSheetModelList = listOf(
        BottomSheetModel(R.drawable.ic_delete, R.string.delete),
        BottomSheetModel(R.drawable.ic_re_upload, R.string.re_upload),
        BottomSheetModel(R.drawable.ic_edit_text, R.string.edit_text)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_bottom_sheet, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        adapter.submitList(bottomSheetModelList)
    }
}