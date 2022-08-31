package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.adapter.EventAdapter
import ru.nikitazar.netology_diploma.adapter.EventOnInteractionListener
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.databinding.FragmentEventsListBinding
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Event
import ru.nikitazar.netology_diploma.ui.EditEventFragment.Companion.longArg
import ru.nikitazar.netology_diploma.utils.drawPlacemark
import ru.nikitazar.netology_diploma.utils.toCoords
import ru.nikitazar.netology_diploma.viewModel.AuthViewModel
import ru.nikitazar.netology_diploma.viewModel.EventViewModel
import ru.nikitazar.netology_diploma.viewModel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class EventsListFragment : Fragment() {

    private val eventViewModel: EventViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val userViewModel: UserViewModel by viewModels(ownerProducer = ::requireParentFragment)
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

        val binding = FragmentEventsListBinding.inflate(inflater, container, false)

        val adapter = EventAdapter(
            object : EventOnInteractionListener {
                override fun onLike(event: Event) {
                    when (event.likedByMe) {
                        true -> eventViewModel.dislikeById(event.id)
                        false -> eventViewModel.likeById(event.id)
                    }
                }

                override fun onEdit(event: Event) {
                    eventViewModel.edit(event)
                }

                override fun onRemove(event: Event) {
                    eventViewModel.removeById(event.id)
                }

                override fun onJoin(event: Event) {
                    when (event.participatedByMe) {
                        false -> eventViewModel.joinById(event.id)
                        true -> eventViewModel.rejectById(event.id)
                    }
                }

                override fun onMap(event: Event) {
                    findNavController().navigate(
                        R.id.action_eventsListFragment_to_bottomSheetDialogEventMapFragment,
                        Bundle().apply {
                            putBoolean("isEdit", false)
                            longArg = event.id
                        }
                    )
                }

                override fun onFullscreenAttachment(attachmentUrl: String) {
                    //TODO("Not yet implemented")
                }

            },
            appAuth,
            userViewModel,
            viewLifecycleOwner
        )

        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            eventViewModel.data.collectLatest(adapter::submitData)
            userViewModel.loadUsers()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventsListFragment_to_editEventFragment)
        }

        eventViewModel.edited.observe(viewLifecycleOwner) { event ->
            adapter.refresh()
            if (event.id != 0L) {
                findNavController().navigate(R.id.action_eventsListFragment_to_editEventFragment)
            }
        }

        return binding.root
    }
}