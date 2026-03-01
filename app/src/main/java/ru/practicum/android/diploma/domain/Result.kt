package ru.practicum.android.diploma.domain

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val code: Int, val message: String? = null) : Result<Nothing>
    data object NoConnection : Result<Nothing>
}
