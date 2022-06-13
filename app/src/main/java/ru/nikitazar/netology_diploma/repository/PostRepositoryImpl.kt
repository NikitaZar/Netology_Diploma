package ru.nikitazar.netology_diploma.repository

import android.util.Log
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import ru.nikitazar.netology_diploma.api.ApiService
import ru.nikitazar.netology_diploma.dao.PostDao
import ru.nikitazar.netology_diploma.dto.AuthState
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.entity.PostEntity
import ru.nikitazar.netology_diploma.errors.ApiError
import ru.nikitazar.netology_diploma.errors.NetworkException
import ru.nikitazar.netology_diploma.errors.UnknownException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

const val PAGE_SIZE = 5
const val ENABLE_PLACE_HOLDERS = false

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService,
    mediator: PostRemoteMediator
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = ENABLE_PLACE_HOLDERS),
        remoteMediator = mediator,
        pagingSourceFactory = { dao.getAll() },
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun updateUser(login: String, pass: String): AuthState {
        try {
            val response = apiService.updateUser(login, pass)
            checkResponse(response)
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun registerUser(login: String, pass: String, name: String): AuthState {
        try {
            val response = apiService.registerUser(login, pass, name)
            checkResponse(response)
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    private fun checkResponse(response: Response<out Any>) {
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        pass: String,
        name: String,
        upload: MediaUpload
    ): AuthState {
        val media = MultipartBody.Part.createFormData(
            "file", upload.file.name, upload.file.asRequestBody()
        )

        val response = apiService.registerWithPhoto(
            login.toRequestBody(),
            pass.toRequestBody(),
            name.toRequestBody(),
            media
        )

        checkResponse(response)
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

}