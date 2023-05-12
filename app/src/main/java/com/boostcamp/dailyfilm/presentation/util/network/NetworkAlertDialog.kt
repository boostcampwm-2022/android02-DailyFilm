package com.boostcamp.dailyfilm.presentation.util.network

import android.content.res.Resources
import com.boostcamp.dailyfilm.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun MaterialAlertDialogBuilder.networkAlertDialog(resources: Resources) =
    MaterialAlertDialogBuilder(context)
        .setTitle(resources.getString(R.string.connect_network))
        .setNegativeButton(resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
        }