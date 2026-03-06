package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.models.FilterSettings
import ru.practicum.android.diploma.domain.repository.PrefsStorageRepository

class SettingsInteractorImpl(
    private val repository: PrefsStorageRepository
) : SettingsInteractor {

    override fun storeFilterSettings(data: FilterSettings) {
        repository.storeFilterSettings(data)
    }

    override fun getFilterSettings(): FilterSettings? {
        return repository.getFilterSettings()
    }
}
