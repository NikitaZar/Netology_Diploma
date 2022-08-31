package ru.nikitazar.netology_diploma.dto

import com.google.gson.annotations.SerializedName
import com.yandex.mapkit.geometry.Point

data class Coords(
    @SerializedName("lat")
    val lat: Float,
    @SerializedName("long")
    val lon: Float
) {
    fun toPoint() = Point(lat.toDouble(), lon.toDouble())
}