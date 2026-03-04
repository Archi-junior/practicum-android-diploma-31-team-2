package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.repository.ShareDataRepository

class ShareDataInteractorImpl(
    private val repository: ShareDataRepository
) : ShareDataInteractor {

    override suspend fun shareUrl(url: String, titleResId: Int) {
        repository.shareUrl(url, titleResId)
    }

    override suspend fun openEmail(email: String) {
        repository.openEmail(email)
    }

    override suspend fun call(number: String) {
        repository.call(number)
    }
}
