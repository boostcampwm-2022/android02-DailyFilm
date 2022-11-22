package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.DialogDatepickerBinding
import java.util.*

class DatePickerDialog(private val calendar: Calendar, private val callback: (Int, Int) -> Unit) : DialogFragment() {

    private var _binding: DialogDatepickerBinding? = null
    private val binding get() = _binding ?: error("Binding is null")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_datepicker, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("DatePickerFragment", "onViewCreated: ${calendar.get(Calendar.YEAR)}, ${calendar.get(Calendar.MONTH)}")

        binding.calendar = calendar

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSet.setOnClickListener {
            callback(
                binding.datePickerSpinner.year,
                binding.datePickerSpinner.month
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
