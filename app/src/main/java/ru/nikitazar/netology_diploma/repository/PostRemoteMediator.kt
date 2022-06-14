package ru.nikitazar.netology_diploma.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.nikitazar.netology_diploma.api.ApiService
import ru.nikitazar.netology_diploma.dao.PostDao
import ru.nikitazar.netology_diploma.dao.PostRemoteKeyDao
import ru.nikitazar.netology_diploma.db.AppDb
import ru.nikitazar.netology_diploma.entity.PostEntity
import ru.nikitazar.netology_diploma.entity.PostRemoteKeyEntity
import ru.nikitazar.netology_diploma.errors.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val db: AppDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> apiService.getLatest(state.config.initialLoadSize)
                LoadType.PREPEND -> {
                    val firstId = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getAfter(firstId, state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val lastId = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(lastId, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        when (db.postRemoteKeyDao().isEmpty()) {
                            true -> postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id),
                                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id),
                                )
                            )
                            false -> postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id),
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id),
                        )
                    }
                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id),
                        )
                    }
                }
            }
            val list = body.map{PostEntity.fromDto(it)}
            Log.i("Mediator posts", body.toString())
            Log.i("Mediator entities", list.toString())
            postDao.insert(body.map{PostEntity.fromDto(it)})

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            Log.i("Mediator error", e.message.toString())
            return MediatorResult.Error(e)
        }
    }
}