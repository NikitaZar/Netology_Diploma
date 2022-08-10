package ru.nikitazar.netology_diploma.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

fun MapView.attachToLifecycle(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(MapViewLifecycleObserver(this))
}

private class MapViewLifecycleObserver(
    private val mapView: MapView
) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                MapKitFactory.getInstance().onStart()
                mapView.onStart()
            }
            Lifecycle.Event.ON_STOP -> {
                mapView.onStop()
                MapKitFactory.getInstance().onStop()
            }
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }
}

fun getUserLocation(defaultLocation: Point, fragment: Fragment): LiveData<Point> {
    @SuppressLint("MissingPermission")
    val requestPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
    val userLocation = MutableLiveData(defaultLocation)
    when (PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.checkSelfPermission(
            fragment.requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val point = Point(location.latitude, location.longitude)
                userLocation.postValue(point)
            }
        }
        else -> {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    return userLocation
}

fun moveToLocation(mapView: MapView, targetLocation: Point) {
    mapView.map.move(
        CameraPosition(targetLocation, 14.0f, 0.0f, 0.0f),
        Animation(Animation.Type.SMOOTH, 5F),
        null
    )
}

fun drawPlacemark(point: Point, mapObjects: MapObjectCollection) {
    val imageProvider = ImageProvider.fromBitmap(drawSimpleBitmap())
    mapObjects.addPlacemark(point, imageProvider)

    Log.i("myLocation", "newPoint: ${point.longitude} x ${point.longitude}")
}

fun drawSimpleBitmap(): Bitmap {
    val picSize = 50
    val bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = Color.GREEN
    paint.style = Paint.Style.FILL
    canvas.drawCircle(
        picSize / 2F,
        picSize / 2F,
        picSize / 2F,
        paint
    )
    return bitmap
}