package ru.nikitazar.netology_diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Post

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
    val coords: Coords? = null,
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

