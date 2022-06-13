package ru.nikitazar.netology_diploma.dto

data class Post(
    val id: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String? = "",
    val content: String = "",
    val published: String = "",
    val coords: Coords = Coords(0F, 0F),
    val link: String? = "",
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null
)