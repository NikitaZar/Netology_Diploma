package ru.nikitazar.netology_diploma.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import dagger.hilt.android.AndroidEntryPoint
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.adapter.UserHorizontalAdapter
import ru.nikitazar.netology_diploma.adapter.UserOnInteractionListener
import ru.nikitazar.netology_diploma.adapter.UserSpinnerAdapter
import ru.nikitazar.netology_diploma.databinding.FragmentEditEventBinding
import ru.nikitazar.netology_diploma.dto.AttachmentType
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Event
import ru.nikitazar.netology_diploma.dto.EventType
import ru.nikitazar.netology_diploma.utils.AndroidUtils
import ru.nikitazar.netology_diploma.utils.LongArg
import ru.nikitazar.netology_diploma.utils.drawPlacemark
import ru.nikitazar.netology_diploma.utils.toCoords
import ru.nikitazar.netology_diploma.view.load
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.EventViewModel
import ru.nikitazar.netology_diploma.viewModel.UserViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

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

    private val eventVewModel: EventViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val userViewModel: UserViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditEventBinding.inflate(inflater, container, false)

        var event = empty
        val speakerIdsData = MutableLiveData(emptyList<Long>())

        eventVewModel.edited.observe(viewLifecycleOwner) {
            event = it
            speakerIdsData.postValue(event.speakerIds)
            bind(event, binding)
        }

        var users = userViewModel.data.value ?: emptyList()
        val speakersSpinner = binding.speakersSpinner
        userViewModel.data.observe(viewLifecycleOwner) {
            users = it
            Log.i("users", users.toString())
            val speakersSpinnerAdapter = UserSpinnerAdapter(users, speakersSpinner.context)
            speakersSpinner.adapter = speakersSpinnerAdapter
        }
        var newSpeakerId = 0L
        val speakersAdapter = UserHorizontalAdapter(object : UserOnInteractionListener {
            override fun onRemove(id: Long) {
                val speakerIds = event.speakerIds.toMutableList()
                speakerIds.remove(id)
                speakerIdsData.postValue(speakerIds)
            }
        })
        binding.speakersList.adapter = speakersAdapter

        speakerIdsData.observe(viewLifecycleOwner) { speakerIds ->
            val speakers = users.filter { user -> user.id in speakerIds }
            speakersAdapter.submitList(speakers)
            event = event.copy(speakerIds = speakerIds)
            Log.i("speakerIds", speakerIds.toString())
        }

        val formatSpinner = binding.formatSpinner
        formatSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val format = formatSpinner.selectedItem.toString()
                event = event.copy(type = EventType.valueOf(format))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        binding.dt.setOnClickListener {
            findNavController().navigate(
                R.id.action_editEventFragment_to_bottomSheetDialogTakeDtFragment,
                Bundle().apply {
                    longArg = event.id
                }
            )
        }

        speakersSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                newSpeakerId = users[i].id
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) = Unit
        }

        binding.btSpeakersAdd.setOnClickListener {
            val speakerIds = event.speakerIds.toMutableList()
            if (!speakerIds.contains(newSpeakerId)) {
                speakerIds.add(newSpeakerId)
            }
            speakerIdsData.postValue(speakerIds)
        }

        binding.takeCoords.setOnClickListener {
            findNavController().navigate(
                R.id.action_editEventFragment_to_bottomSheetDialogEventMapFragment,
                Bundle().apply {
                    putBoolean("isEdit", true)
                    Log.i("mapView", "eventSrc: $event") //TODO debug
                    longArg = event.id
                }
            )
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
            event = event.copy(
                content = binding.content.text.toString(),
                link = binding.link.text.toString()
            )
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

            try {
                val formatterGetDt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val formatterSetDt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                when (event.datetime.isNotEmpty()) {
                    true -> {
                        val dtFormated = formatterGetDt.parse(event.datetime)
                        dt.setText(formatterSetDt.format(dtFormated as Date))
                    }
                    false -> Unit
                }
            } catch (e: ParseException) {
                Log.e("EditEventFragment", e.message.toString())
            }

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