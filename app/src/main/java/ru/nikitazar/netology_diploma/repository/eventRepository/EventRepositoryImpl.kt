package ru.nikitazar.netology_diploma.repository.eventRepository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.nikitazar.netology_diploma.api.EventApiService
import ru.nikitazar.netology_diploma.dao.EventDao
import ru.nikitazar.netology_diploma.dto.*
import ru.nikitazar.netology_diploma.entity.EventEntity
import ru.nikitazar.netology_diploma.errors.*
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

const val PAGE_SIZE = 5
const val ENABLE_PLACE_HOLDERS = false

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val dao: EventDao,
    private val apiService: EventApiService,
    mediator: EventRemoteMediator
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = ENABLE_PLACE_HOLDERS),
        remoteMediator = mediator,
        pagingSourceFactory = { dao.getAll() },
    ).flow.map { pagingData ->
        pagingData.map(EventEntity::toDto)
    }

    override suspend fun getById(id: Long) = dao.getById(id)?.toDto() ?: throw DbError

    override suspend fun getMaxId() = dao.getMaxId()?.toDto()?.id ?: throw DbError

    override suspend fun likeById(id: Long) {
        try {
            val response = apiService.likeById(id)
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = apiService.dislikeById(id)
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun save(event: Event, retry: Boolean) {
        try {
            val response = apiService.save(event)
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun uploadMedia(upload: MediaUpload): Attachment {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
            checkResponse(response)
            val mediaResponse = response.body() ?: throw ApiError(response.code(), response.message())
            return Attachment(mediaResponse.url, AttachmentType.IMAGE)
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun joinById(id: Long) {
        try {
            val response = apiService.participate(id)
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun rejectById(id: Long) {
        try {
            val response = apiService.rejection(id)
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun saveWithAttachment(event: Event, upload: MediaUpload, retry: Boolean) {
        try {
            val attachment = uploadMedia(upload)
            val eventWithAttachment = event.copy(attachment = attachment)
            save(eventWithAttachment, retry)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.removeById(id)
            checkResponse(response)
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }
}

private fun checkResponse(response: Response<out Any>) {
    if (!response.isSuccessful) {
        throw ApiError(response.code(), response.message())
    }
}