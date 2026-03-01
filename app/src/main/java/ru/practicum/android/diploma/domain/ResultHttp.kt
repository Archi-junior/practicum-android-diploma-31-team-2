package ru.practicum.android.diploma.domain

sealed interface ResultHttp<out T> {
    data class Success<T>(val data: T) : ResultHttp<T>
    data class Error(val code: Int, val message: String? = null) : ResultHttp<Nothing>
    data object NoConnection : ResultHttp<Nothing>
}
