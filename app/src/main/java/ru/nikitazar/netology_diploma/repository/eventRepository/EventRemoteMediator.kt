package ru.nikitazar.netology_diploma.repository.eventRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.nikitazar.netology_diploma.api.EventApiService
import ru.nikitazar.netology_diploma.dao.EventDao
import ru.nikitazar.netology_diploma.dao.EventRemoteKeyDao
import ru.nikitazar.netology_diploma.db.AppDb
import ru.nikitazar.netology_diploma.entity.*
import ru.nikitazar.netology_diploma.errors.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator @Inject constructor(
    private val apiService: EventApiService,
    private val dao: EventDao,
    private val remoteKeyDao: EventRemoteKeyDao,
    private val db: AppDb
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, EventEntity>): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> apiService.getLatest(state.config.initialLoadSize)
                LoadType.PREPEND -> {
                    val firstId = remoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getAfter(firstId, state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val lastId = remoteKeyDao.min() ?: return MediatorResult.Success(false)
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
                            true -> remoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(RemoteKeyType.BEFORE, body.last().id),
                                    EventRemoteKeyEntity(RemoteKeyType.AFTER, body.first().id),
                                )
                            )
                            false -> remoteKeyDao.insert(
                                EventRemoteKeyEntity(RemoteKeyType.BEFORE, body.last().id),
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        remoteKeyDao.insert(
                            EventRemoteKeyEntity(RemoteKeyType.BEFORE, body.last().id),
                        )
                    }
                    LoadType.PREPEND -> {
                        remoteKeyDao.insert(
                            EventRemoteKeyEntity(RemoteKeyType.AFTER, body.first().id),
                        )
                    }
                }
            }
            val list = body.map{EventEntity.fromDto(it)}
            Log.i("Mediator posts", body.toString())
            Log.i("Mediator entities", list.toString())
            dao.insert(body.map{EventEntity.fromDto(it)})

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            Log.e("Mediator error", e.message.toString())
            return MediatorResult.Error(e)
        }
    }
}