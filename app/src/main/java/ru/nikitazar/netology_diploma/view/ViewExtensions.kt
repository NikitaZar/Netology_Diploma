package ru.nikitazar.netology_diploma.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop

fun ImageView.load(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    Glide.with(this)
        .load(url)
        .timeout(10_000)
        .transform(*transforms)
        .into(this)

fun ImageView.loadCircleCrop(url: String, placeholderId: Int) =
    Glide.with(this)
        .load(url)
        .timeout(10_000)
        .transform(CircleCrop())
        .placeholder(placeholderId)
        .into(this)