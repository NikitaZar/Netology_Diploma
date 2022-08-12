package ru.nikitazar.netology_diploma.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.mapview.MapView
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.utils.attachToLifecycle
import ru.nikitazar.netology_diploma.utils.moveToUserLocation
import ru.nikitazar.netology_diploma.utils.toCoords

class BottomSheetDialogMap(
    context: Context,
    @StyleRes
    them: Int,
    @StyleRes
    resMap: Int,
    @StyleRes
    resLayout: Int,
    view: View?,
    @StyleRes
    resMapView: Int,
    inputListener: InputListener,
    @StyleRes
    val resBtChangeCoords: Int,
    @StyleRes
    val resBtDeleteCoords: Int,
    lifecycleOwner: LifecycleOwner,
    isVisibleBt: Boolean,
) : BottomSheetDialog(context, them) {

    private val bottomSheetViewRoot = view?.findViewById<CoordinatorLayout>(resMap)
    private val bottomSheetView = LayoutInflater.from(context).inflate(resLayout, bottomSheetViewRoot).also {
        it.findViewById<MaterialButton>(resBtChangeCoords)?.isVisible = isVisibleBt
        it.findViewById<MaterialButton>(resBtDeleteCoords)?.isVisible = isVisibleBt
    }

    private val mapView = bottomSheetView.findViewById<MapView>(resMapView)
        .apply {
            attachToLifecycle(lifecycleOwner)
            map.addInputListener(inputListener)
            setContentView(bottomSheetView)
            show()
        }

    val mapObjects = mapView.map.mapObjects.addCollection()

    fun moveToLocation(coords: Coords) = ru.nikitazar.netology_diploma.utils.moveToLocation(mapView, coords.toPoint())

    fun drawPlacemark(coords: Coords) = ru.nikitazar.netology_diploma.utils.drawPlacemark(coords.toPoint(), mapObjects)

    fun moveToDefaultLocation(fragment: Fragment) {
        moveToUserLocation(fragment).observe(fragment.viewLifecycleOwner) {
            moveToLocation(it.toCoords())
        }
    }

    fun onChangeCoords(changeAction: () -> Unit) {
        bottomSheetView.findViewById<MaterialButton>(resBtChangeCoords).setOnClickListener {
            changeAction()
            dismiss()
        }
    }

    fun onDeleteCoords(changeAction: () -> Unit) {
        bottomSheetView.findViewById<MaterialButton>(resBtDeleteCoords).setOnClickListener {
            changeAction()
            mapView.map.mapObjects.clear()
        }
    }
}