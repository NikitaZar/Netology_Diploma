package ru.nikitazar.netology_diploma.entity

import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Post
import java.lang.NumberFormatException
import java.lang.StringBuilder

const val LIST_DELIMITER = ','

@Entity
data class PostEntity(
    @PrimaryKey
    val id: Long,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String? = "",
    val content: String = "",
    val published: String = "",
    @Embedded
    val coords: Coords = Coords(0F, 0F),
    val link: String?,
    val mentionIds: String = "",
    val mentionedMe: Boolean = false,
    val likeOwnerIds: String = "",
    val likedByMe: Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorAvatar,
        content,
        published,
        coords,
        link,
        mentionIds.toDto(),
        mentionedMe,
        likeOwnerIds.toDto(),
        likedByMe,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.coords,
                dto.link,
                dto.mentionIds.fromDto(),
                dto.mentionedMe,
                dto.likeOwnerIds.fromDto(),
                dto.likedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}

fun List<PostEntity>.toDto() = map { it.toDto() }

fun List<Post>.toEntity() = map { PostEntity.fromDto(it) }

@JvmName("toEntityLong")
fun List<Long>.fromDto(): String {
    val sb = StringBuilder()
    for (i in 0 until this.size) {
        sb.append(this[i])
        if (i < this.size - 1) {
            sb.append(LIST_DELIMITER)
        }
    }
    return sb.toString()
}

fun String.toDto(): List<Long> {
    if (this == "") {
        return emptyList()
    }
    return this.split(LIST_DELIMITER).map { it.toLong() }
}
