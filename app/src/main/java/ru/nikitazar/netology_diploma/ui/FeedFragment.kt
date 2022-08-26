package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.adapter.FeedAdapter
import ru.nikitazar.netology_diploma.adapter.FeedOnInteractionListener
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.databinding.FragmentFeedBinding
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.ui.EditEventFragment.Companion.longArg
import ru.nikitazar.netology_diploma.utils.drawPlacemark
import ru.nikitazar.netology_diploma.utils.toCoords
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

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

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(context)
        mapKit = MapKitFactory.getInstance()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        authViewModel.data.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                findNavController().navigate(R.id.authFragment)
            }
        }

        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(
            object : FeedOnInteractionListener {
                override fun onEdit(post: Post) {
                    postViewModel.edit(post)
                }

                override fun onLike(post: Post) {
                    when (post.likedByMe) {
                        true -> postViewModel.dislikeById(post.id)
                        false -> postViewModel.likeById(post.id)
                    }
                }

                override fun onRemove(post: Post) {
                    postViewModel.removeById(post.id)
                }

                override fun onMap(coords: Coords) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_bottomSheetDialogMapFragment,
                        Bundle().apply { putBoolean("isEdit", false) }
                    )
                }

                override fun onFullscreenAttachment(attachmentUrl: String) {
                    //TODO
                }
            },
            appAuth
        )

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            postViewModel.data.collectLatest(adapter::submitData)
        }

        postViewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            binding.progress.isVisible = dataState.loading

            if (dataState.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) {}.show()
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
        }

        postViewModel.edited.observe(viewLifecycleOwner) { post ->
            adapter.refresh()
            if (post.id != 0L) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply { longArg = post.id })
            }
        }

        return binding.root
    }
}