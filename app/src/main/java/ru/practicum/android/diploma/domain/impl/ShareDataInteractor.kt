package ru.practicum.android.diploma.domain.impl

interface ShareDataInteractor {
    suspend fun shareUrl(url: String, title: String)
    suspend fun openEmail(email: String)
    suspend fun call(number: String)
}
