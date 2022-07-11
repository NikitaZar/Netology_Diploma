package ru.nikitazar.netology_diploma.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.FragmentEditEventBinding
import ru.nikitazar.netology_diploma.dto.AttachmentType
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Event
import ru.nikitazar.netology_diploma.dto.EventType
import ru.nikitazar.netology_diploma.utils.AndroidUtils
import ru.nikitazar.netology_diploma.utils.LongArg
import ru.nikitazar.netology_diploma.view.load
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.EventViewModel

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    datetime = "",
    published = "",
    coords = Coords(0F, 0F),
    type = EventType.OFFLINE,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    speakerIds = emptyList(),
    participantsIds = emptyList(),
    participatedByMe = false,
    attachment = null,
    link = null,
)

@AndroidEntryPoint
class EditEventFragment : Fragment() {

    companion object {
        var Bundle.longArg: Long? by LongArg
    }

    private val eventVewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditEventBinding.inflate(inflater, container, false)

        arguments?.longArg?.let { id -> eventVewModel.getById(id) }
        val event = empty
        bind(event, binding)

        eventVewModel.eventById.observe(viewLifecycleOwner) {
            bind(it, binding)
        }

        binding.dt.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(it.context, R.style.BottomSheetDialogThem)
            val bottomSheetViewRoot = view?.findViewById<LinearLayout>(R.id.bottom_sheet_calendar)
            val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_dt, bottomSheetViewRoot)



            bottomSheetView.findViewById<MaterialButton>(R.id.bt_ok).setOnClickListener {
                val timePicker = bottomSheetView.findViewById<TimePicker>(R.id.time_picker)
                timePicker.setIs24HourView(true)
                val hour = timePicker.hour
                val minute = timePicker.minute

                val datePicker = bottomSheetView.findViewById<DatePicker>(R.id.date_picker)
                val dayOfMonth = datePicker.dayOfMonth
                val month = datePicker.month
                val year = datePicker.year
                binding.dt.setText("$year-${month + 1}-${dayOfMonth} $hour:$minute")

                Log.i("bottomSheetView", "$year-${month + 1}-${dayOfMonth}T$hour:$minute:00.000Z")

                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

        binding.btParticipantsAdd.setOnClickListener {
            //TODO
        }

        binding.btSpeakersAdd.setOnClickListener {
            //TODO
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            eventVewModel.cancelEdit()
            findNavController().navigateUp()
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        eventVewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.takeImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            eventVewModel.changePhoto(null, null)
        }

        binding.ok.setOnClickListener {
            eventVewModel.save(event)
            AndroidUtils.hideKeyboard(requireView())
        }

        eventVewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        eventVewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.data.value?.id == 0L) {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private fun bind(event: Event, binding: FragmentEditEventBinding) {
        with(binding) {
            content.setText(event.content)
            dt.setText(event.datetime)
            link.setText(event.link)
            event.attachment?.let { attachment ->
                when (attachment.type) {
                    AttachmentType.IMAGE -> photo.load(event.attachment.url)
                    else -> Unit
                }
            }
        }
    }
}