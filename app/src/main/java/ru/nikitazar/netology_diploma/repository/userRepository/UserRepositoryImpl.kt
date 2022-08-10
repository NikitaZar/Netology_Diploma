package ru.nikitazar.netology_diploma.repository.userRepository

import androidx.lifecycle.asLiveData
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import ru.nikitazar.netology_diploma.api.UserApiService
import ru.nikitazar.netology_diploma.dao.UserDao
import ru.nikitazar.netology_diploma.dto.User
import ru.nikitazar.netology_diploma.entity.UserEntity
import ru.nikitazar.netology_diploma.entity.toDto
import ru.nikitazar.netology_diploma.entity.toEntity
import ru.nikitazar.netology_diploma.errors.ApiError
import ru.nikitazar.netology_diploma.errors.NetworkException
import ru.nikitazar.netology_diploma.errors.UnknownException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val apiService: UserApiService
) : UserRepository {

    override val data = dao.getAll()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)
        .asLiveData()

    override suspend fun getById(id: Long): User {
        try {
            val response = apiService.getById(id)
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(UserEntity.fromDto(body))
            return body
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            checkResponse(response)
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: ApiException) {
            throw e
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