package ru.practicum.android.diploma.domain

sealed interface ResultDb<out T> {
    data class Success<T>(val data: T) : ResultDb<T>
    data class Error(val message: String? = null) : ResultDb<Nothing>
}
