package ru.practicum.android.diploma.util

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val code: Int, val message: String? = null) : UiState<Nothing>()
    object NoConnection : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
