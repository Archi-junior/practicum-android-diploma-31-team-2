package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.repository.ShareDataRepository

class ShareDataInteractorImpl(
    val repository: ShareDataRepository
) : ShareDataInteractor{
    override suspend fun shareUrl(url: String, title: String) {
        repository.shareUrl(url, title)
    }

    override suspend fun openEmail(email: String) {
        repository.openEmail(email)
    }

    override suspend fun call(number: String) {
        repository.call(number)
    }
}
