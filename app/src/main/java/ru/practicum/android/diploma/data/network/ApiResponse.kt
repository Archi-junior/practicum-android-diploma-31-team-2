package ru.practicum.android.diploma.data.network

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String? = null) : ApiResponse<Nothing>()
    data object NoConnection : ApiResponse<Nothing>()
}
