package ru.nikitazar.netology_diploma.model

data class RegistrationErrorState(
    val error: Boolean = false,
    val type: RegistrationErrorType = RegistrationErrorType.NO_ERROR
)

enum class RegistrationErrorType {
    REGISTERED, NO_ERROR
}