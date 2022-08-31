package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.nikitazar.netology_diploma.databinding.FragmentBottomSheetDialogTakeDtBinding
import ru.nikitazar.netology_diploma.ui.EditEventFragment.Companion.longArg
import ru.nikitazar.netology_diploma.viewModel.EventViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetDialogTakeDtFragment : BottomSheetDialogFragment() {

    private val eventVewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @Inject
    lateinit var calendar: Calendar

    private val formatterSetDt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val formatterGetDt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomSheetDialogTakeDtBinding.inflate(inflater, container, false)

        val timePicker = binding.timePicker.apply { setIs24HourView(DateFormat.is24HourFormat(context)) }
        val datePicker = binding.datePicker

        arguments?.longArg?.let { id -> eventVewModel.getById(id) }

        eventVewModel.eventById.observe(viewLifecycleOwner) { event ->
            try {
                calendar.time = formatterGetDt.parse(event.datetime) as Date

                timePicker.hour = calendar.get(Calendar.HOUR)
                timePicker.minute = calendar.get(Calendar.MINUTE)

                datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            } catch (e: ParseException) {
                Log.e("BottomSheetDialogTakeDtFragment", e.message.toString())
            }
        }

        binding.btOk.setOnClickListener {
            calendar.set(Calendar.HOUR, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            calendar.set(Calendar.MONTH, datePicker.month)
            calendar.set(Calendar.YEAR, datePicker.year)
            val dt = formatterSetDt.format(calendar.time).toString()
            eventVewModel.changeDt(dt)
            dismiss()
        }

        return binding.root
    }
}