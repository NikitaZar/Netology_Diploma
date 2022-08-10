package ru.nikitazar.netology_diploma.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nikitazar.netology_diploma.dto.User

@Entity
data class UserEntity(
    @PrimaryKey
    val id: Long = 0,
    val login: String = "",
    val name: String = "",
    val avatar: String? = null
) {
    fun toDto() = User(
        id,
        login,
        name,
        avatar
    )

    companion object {
        fun fromDto(dto: User) =
            UserEntity(
                dto.id,
                dto.login,
                dto.name,
                dto.avatar
            )
    }
}

fun List<UserEntity>.toDto() = map { it.toDto() }

fun List<User>.toEntity() = map { UserEntity.fromDto(it) }