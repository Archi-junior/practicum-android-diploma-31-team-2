package ru.practicum.android.diploma.domain.repository

interface ShareDataRepository {
    suspend fun shareUrl(url: String, title: String)
    suspend fun openEmail(email: String)
    suspend fun call(number: String)
}
