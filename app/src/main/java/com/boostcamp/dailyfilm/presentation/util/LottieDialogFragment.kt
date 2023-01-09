package com.boostcamp.dailyfilm.presentation.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.boostcamp.dailyfilm.R


class LottieDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_lottie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        setStyle(STYLE_NO_FRAME, R.style.Theme_DailyFilm)
    }

     fun showProgressDialog(sp : FragmentManager) {
        if (this.isAdded.not()) {
            this.show(sp, "loader")
        }
    }

     fun hideProgressDialog() {
        if (this.isAdded) {
            this.dismissAllowingStateLoss()
        }
    }
    companion object {

        fun newInstance(): LottieDialogFragment {
            val args = Bundle()
            val fragment = LottieDialogFragment()
            fragment.arguments = args
            fragment.isCancelable=false
            return fragment

        }
    }
}