package ru.nikitazar.netology_diploma.repository.eventRepository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.nikitazar.netology_diploma.dto.*

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun save(event: Event, retry: Boolean)
    suspend fun removeById(id: Long)
    suspend fun saveWithAttachment(event: Event, upload: MediaUpload, retry: Boolean)
    suspend fun getById(id: Long): Event
    suspend fun getMaxId(): Long
    suspend fun uploadMedia(upload: MediaUpload): Media
}