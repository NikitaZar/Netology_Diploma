package ru.nikitazar.netology_diploma.dto

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Long = 0L,
    @SerializedName("authorId")
    val authorId: Long = 0L,
    @SerializedName("author")
    val author: String = "",
    @SerializedName("authorAvatar")
    val authorAvatar: String? = null,
    @SerializedName("content")
    val content: String = "",
    @SerializedName("datetime")
    val datetime: String = "",
    @SerializedName("published")
    val published: String = "",
    @SerializedName("coords")
    val coords: Coords = Coords(0F, 0F),
    @SerializedName("type")
    val type: EventType = EventType.OFFLINE,
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Long> = emptyList(),
    @SerializedName("likedByMe")
    val likedByMe: Boolean = false,
    @SerializedName("speakerIds")
    val speakerIds: List<Long> = emptyList(),
    @SerializedName("participantsIds")
    val participantsIds: List<Long> = emptyList(),
    @SerializedName("participatedByMe")
    val participatedByMe: Boolean = false,
    @SerializedName("attachment")
    val attachment: Attachment? = null,
    @SerializedName("link")
    val link: String? = null
)
