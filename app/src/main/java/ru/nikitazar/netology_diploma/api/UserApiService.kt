package ru.nikitazar.netology_diploma.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.nikitazar.netology_diploma.dto.User

interface UserApiService {

    @GET("users")
    suspend fun getAll(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getById(@Path("id") id: Long): Response<User>
}