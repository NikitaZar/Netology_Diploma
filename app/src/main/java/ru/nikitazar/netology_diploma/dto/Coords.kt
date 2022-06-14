package ru.nikitazar.netology_diploma.dto

import com.google.gson.annotations.SerializedName

data class Coords(
    @SerializedName("lat")
    val lat: Float,
    @SerializedName("long")
    val lon: Float
)