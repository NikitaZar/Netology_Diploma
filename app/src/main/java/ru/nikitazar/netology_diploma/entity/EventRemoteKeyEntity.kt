package ru.nikitazar.netology_diploma.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Long
)