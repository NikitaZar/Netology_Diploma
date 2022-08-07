package ru.nikitazar.netology_diploma.model

data class AuthErrorState(
    val error: Boolean = false,
    val type: AuthErrorType = AuthErrorType.NO_ERROR
)

enum class AuthErrorType {
    PASSWORD ,REGISTERED, NO_ERROR
}