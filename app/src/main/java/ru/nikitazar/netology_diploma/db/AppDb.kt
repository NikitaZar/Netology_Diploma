package ru.nikitazar.netology_diploma.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nikitazar.netology_diploma.dao.EventDao
import ru.nikitazar.netology_diploma.dao.EventRemoteKeyDao
import ru.nikitazar.netology_diploma.dao.PostDao
import ru.nikitazar.netology_diploma.dao.PostRemoteKeyDao
import ru.nikitazar.netology_diploma.entity.EventEntity
import ru.nikitazar.netology_diploma.entity.EventRemoteKeyEntity
import ru.nikitazar.netology_diploma.entity.PostEntity
import ru.nikitazar.netology_diploma.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, EventEntity::class, EventRemoteKeyEntity::class], version = 2)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao

}