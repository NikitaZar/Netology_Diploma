package ru.nikitazar.netology_diploma.repository.postRepository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.nikitazar.netology_diploma.dto.*

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun save(post: Post, retry: Boolean)
    suspend fun removeById(id: Long)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload, retry: Boolean)
    suspend fun getPostById(id: Long): Post
    suspend fun getMaxId(): Long
    suspend fun uploadMedia(upload: MediaUpload): Attachment
}