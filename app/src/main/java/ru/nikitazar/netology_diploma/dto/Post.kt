package ru.nikitazar.netology_diploma.dto

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("authorId")
    val authorId: Long = 0,
    @SerializedName("author")
    val author: String = "",
    @SerializedName("authorAvatar")
    val authorAvatar: String? = "",
    @SerializedName("content")
    val content: String = "",
    @SerializedName("published")
    val published: String = "",
    @SerializedName("coords")
    val coords: Coords = Coords(0F, 0F),
    @SerializedName("link")
    val link: String? = "",
    @SerializedName("mentionIds")
    val mentionIds: List<Long> = emptyList(),
    @SerializedName("mentionedMe")
    val mentionedMe: Boolean = false,
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Long> = emptyList(),
    @SerializedName("likedByMe")
    val likedByMe: Boolean = false,
    @SerializedName("attachment")
    val attachment: Attachment? = null,
)