package ru.nikitazar.netology_diploma.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nikitazar.netology_diploma.dao.*
import ru.nikitazar.netology_diploma.entity.*

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        UserEntity::class
    ],
    version = 6
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun userDao(): UserDao
}