package ru.nikitazar.netology_diploma.dto

data class Post(
    val id: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    val published: String = "",
    val coords: Coords = Coords(0F, 0F),
    val attachment: Attachment? = null
)