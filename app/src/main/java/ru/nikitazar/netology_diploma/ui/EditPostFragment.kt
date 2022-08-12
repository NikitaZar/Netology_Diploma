package ru.nikitazar.netology_diploma.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import dagger.hilt.android.AndroidEntryPoint
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.FragmentEditPostBinding
import ru.nikitazar.netology_diploma.dto.AttachmentType
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.ui.EditEventFragment.Companion.longArg
import ru.nikitazar.netology_diploma.utils.*
import ru.nikitazar.netology_diploma.view.load
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.PostViewModel

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    coords = Coords(0F, 0F),
    link = null,
    mentionIds = emptyList(),
    mentionedMe = false,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    attachment = null
)

@AndroidEntryPoint
class EditPostFragment : Fragment() {

    private lateinit var mapKit: MapKit
    private var coords: Coords? = null
    private lateinit var mapObjects: MapObjectCollection

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            //nothing to do
        }

        override fun onMapLongTap(map: Map, point: Point) {
            coords = point.toCoords()
            drawPlacemark(point, mapObjects)
        }
    }

    private val postVewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(context)
        mapKit = MapKitFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )

        var post = empty
        bind(post, binding)

        arguments?.longArg?.let { id -> postVewModel.getById(id) }
        postVewModel.postById.observe(viewLifecycleOwner) {
            post = it
            coords = post.coords
            bind(it, binding)
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
                        postVewModel.changePhoto(uri, uri?.toFile())
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
            postVewModel.changePhoto(null, null)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            postVewModel.cancelEdit()
            findNavController().navigateUp()
        }

        binding.ok.setOnClickListener {
            postVewModel.save(binding.edit.text.toString())
            AndroidUtils.hideKeyboard(requireView())
        }

        postVewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        postVewModel.photo.observe(viewLifecycleOwner) {
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

        binding.takeCoords.setOnClickListener {
            val bottomSheetDialogMap = BottomSheetDialogMap(
                it.context,
                R.style.BottomSheetDialogThem,
                R.id.bottom_sheet_map,
                R.layout.layout_bottom_sheet_map,
                view,
                R.id.mapview,
                inputListener,
                R.id.bt_ok,
                R.id.bt_delete,
                viewLifecycleOwner,
                true
            ).apply {
                onChangeCoords {
                    coords?.let { postVewModel.changeCoords(it) }
                }
                onDeleteCoords {
                    coords = null
                }
            }.also { map ->
                mapObjects = map.mapObjects
            }

            coords?.let {
                bottomSheetDialogMap.apply {
                    moveToLocation(it)
                    drawPlacemark(it)
                }
            } ?: run {
                //bottomSheetDialogMap.moveToDefaultLocation(this)
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun bind(post: Post, binding: FragmentEditPostBinding) {
        with(binding) {
            edit.setText(post.content)
            edit.requestFocus()
            post.attachment?.let { attachment ->
                when (attachment.type) {
                    AttachmentType.IMAGE -> photo.load(post.attachment.url)
                    else -> Unit
                }
            }
        }
    }
}