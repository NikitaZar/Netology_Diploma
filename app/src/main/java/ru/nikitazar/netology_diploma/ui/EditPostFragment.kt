package ru.nikitazar.netology_diploma.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.AndroidEntryPoint
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.FragmentEditPostBinding
import ru.nikitazar.netology_diploma.utils.AndroidUtils
import ru.nikitazar.netology_diploma.utils.StringArg
import ru.nikitazar.netology_diploma.utils.attachToLifecycle
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.PostViewModel

@AndroidEntryPoint
class EditPostFragment : Fragment() {

    private lateinit var mapKit: MapKit
    private lateinit var mapObjects: MapObjectCollection

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            //nothing to do
        }

        override fun onMapLongTap(map: Map, point: Point) {
            //TODO set Point on Map
        }
    }

    companion object {
        var Bundle.textArg: String? by StringArg
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

        arguments?.textArg?.let(binding.edit::setText)
        binding.edit.requestFocus()

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
            val bottomSheetDialog = BottomSheetDialog(it.context, R.style.BottomSheetDialogThem)
            val bottomSheetViewRoot = view?.findViewById<LinearLayout>(R.id.bottom_sheet_map)
            val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_map, bottomSheetViewRoot)

            val mapView = bottomSheetView.findViewById<MapView>(R.id.mapview)
            mapView.map.addInputListener(inputListener)
            mapObjects = mapView.map.mapObjects.addCollection()
            mapView.attachToLifecycle(viewLifecycleOwner)

            bottomSheetView.findViewById<MaterialButton>(R.id.bt_ok).setOnClickListener {
                //TODO set Coords

                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
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
}