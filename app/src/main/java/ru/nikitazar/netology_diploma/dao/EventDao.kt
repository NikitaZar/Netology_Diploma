package ru.nikitazar.netology_diploma.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.nikitazar.netology_diploma.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getAll(): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<EventEntity>)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM EventEntity")
    suspend fun removeAll()

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getPostById(id: Long): EventEntity?

    @Query("SELECT * FROM EventEntity ORDER BY id DESC LIMIT 1")
    suspend fun getPostMaxId(): EventEntity?
}