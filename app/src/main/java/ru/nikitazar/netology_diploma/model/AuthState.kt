package ru.nikitazar.netology_diploma.model

data class AuthState(
    val id: Long = 0,
    val token: String? = null,
    @Transient
    val login: String? = null
)