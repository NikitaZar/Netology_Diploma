package ru.nikitazar.netology_diploma.repository.authRepository

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import ru.nikitazar.netology_diploma.api.AuthApiService
import ru.nikitazar.netology_diploma.dto.AuthState
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.errors.ApiError
import ru.nikitazar.netology_diploma.errors.ApiError2
import ru.nikitazar.netology_diploma.errors.NetworkException
import ru.nikitazar.netology_diploma.errors.UnknownException
import ru.nikitazar.netology_diploma.repository.postRepository.checkResponse
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {
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
            return response.body() ?: throw ApiError2(response.code(), response.message(), response.raw().body)
        } catch (e: IOException) {
            throw NetworkException
//        } catch (e: Exception) {
//            throw UnknownException
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        upload: MediaUpload
    ): AuthState {
        val media = MultipartBody.Part.createFormData(
            "file", upload.file.name, upload.file.asRequestBody()
        )

        val response = apiService.registerWithPhoto(
            login.toRequestBody(),
            password.toRequestBody(),
            name.toRequestBody(),
            media
        )

        checkResponse(response)
        return response.body() ?: throw ApiError2(response.code(), response.message(), response.raw().body)
    }

    private fun checkResponse(response: Response<out Any>) {
        if (!response.isSuccessful) {
            throw ApiError2(response.code(), response.message(), response.errorBody())
        }
    }
}