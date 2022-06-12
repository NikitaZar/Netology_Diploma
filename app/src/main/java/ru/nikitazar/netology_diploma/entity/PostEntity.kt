package ru.nikitazar.netology_diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    val published: String = "",
    val coordsLat: Float = 0F,
    val coordsLong: Float = 0F,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post(id,
        authorId,
        author,
        authorAvatar,
        content,
        published,
        Coords(coordsLat, coordsLong),
        attachment?.toDto())

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.coords.lat,
                dto.coords.long,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}

fun List<PostEntity>.toDto() = map { it.toDto() }

fun List<Post>.toEntity() = map { PostEntity.fromDto(it) }