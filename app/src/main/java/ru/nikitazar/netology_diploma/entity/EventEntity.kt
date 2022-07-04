package ru.nikitazar.netology_diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nikitazar.netology_diploma.dto.*

@Entity
data class EventEntity(
    @PrimaryKey
    val id: Long = 0L,
    val authorId: Long = 0L,
    val author: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    val datetime: String = "",
    val published: String = "",
    @Embedded
    val coords: Coords = Coords(0F, 0F),
    val eventType: EventType = EventType.OFFLINE,
    val likeOwnerIds: String = "",
    val likedByMe: Boolean = false,
    val speakerIds: String = "",
    val participantsIds: String = "",
    val participatedByMe: Boolean = false,
    @Embedded
    var attachment: Attachment?,
    var link: String?
) {
    fun toDto() = Event(
        id,
        authorId,
        author,
        authorAvatar,
        content,
        datetime,
        published,
        coords,
        eventType,
        likeOwnerIds.toDto(),
        likedByMe,
        speakerIds.toDto(),
        participantsIds.toDto(),
        participatedByMe,
        attachment,
        link
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.datetime,
                dto.published,
                dto.coords,
                dto.type,
                dto.likeOwnerIds.fromDto(),
                dto.likedByMe,
                dto.speakerIds.fromDto(),
                dto.participantsIds.fromDto(),
                dto.participatedByMe,
                dto.attachment,
                dto.link
            )
    }
}

fun List<EventEntity>.toDto() = map { it.toDto() }

fun List<Event>.toEntity() = map { EventEntity.fromDto(it) }