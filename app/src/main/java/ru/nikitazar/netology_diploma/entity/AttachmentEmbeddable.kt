package ru.nikitazar.netology_diploma.entity

import ru.nikitazar.netology_diploma.dto.Attachment
import ru.nikitazar.netology_diploma.dto.AttachmentType

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType?,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}