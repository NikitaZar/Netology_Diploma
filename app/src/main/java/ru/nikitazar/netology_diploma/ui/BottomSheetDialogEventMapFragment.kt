package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import ru.nikitazar.netology_diploma.databinding.FragmentBottomSheetDialogPostMapBinding
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.ui.EditEventFragment.Companion.longArg
import ru.nikitazar.netology_diploma.utils.attachToLifecycle
import ru.nikitazar.netology_diploma.utils.drawPlacemark
import ru.nikitazar.netology_diploma.utils.moveToLocation
import ru.nikitazar.netology_diploma.utils.toCoords
import ru.nikitazar.netology_diploma.viewModel.EventViewModel
import ru.nikitazar.netology_diploma.viewModel.PostViewModel

class BottomSheetDialogEventMapFragment : BottomSheetDialogFragment() {

    private val eventVewModel: EventViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var coords: Coords? = null
    private lateinit var mapKit: MapKit
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(context)
        mapKit = MapKitFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentBottomSheetDialogPostMapBinding.inflate(inflater, container, false)

        val mapView = binding.mapview.apply {
            attachToLifecycle(viewLifecycleOwner)
            map.addInputListener(inputListener)
            mapObjects = map.mapObjects.addCollection()
            Log.i("mapView", "mapObjects") //TODO debug
        }

        arguments?.longArg?.let { id -> eventVewModel.getById(id) }
        eventVewModel.eventById.observe(viewLifecycleOwner) { event ->
            Log.i("mapView", "eventDst: $event")  //TODO debug
            mapObjects.clear()
            event.coords?.let { coords ->
                moveToLocation(mapView, coords.toPoint())
                drawPlacemark(coords.toPoint(), mapObjects)
            } ?: run {
                //bottomSheetDialogMap.moveToDefaultLocation(this)
            }
        }

        binding.btOk.apply {
            isVisible = arguments?.getBoolean("isEdit") ?: false
            setOnClickListener {
                coords?.let { eventVewModel.changeCoords(it) }
                dismiss()
            }
        }

        binding.btDelete.apply {
            isVisible = arguments?.getBoolean("isEdit") ?: false
            setOnClickListener {
                eventVewModel.changeCoords(null)
                mapView.map.mapObjects.clear()
            }
        }

        return binding.root
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}