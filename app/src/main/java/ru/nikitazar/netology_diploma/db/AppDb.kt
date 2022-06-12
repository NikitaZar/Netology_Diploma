package ru.nikitazar.netology_diploma.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nikitazar.netology_diploma.dao.PostDao
import ru.nikitazar.netology_diploma.dao.PostRemoteKeyDao
import ru.nikitazar.netology_diploma.entity.PostEntity
import ru.nikitazar.netology_diploma.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}