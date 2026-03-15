package ru.practicum.android.diploma.domain.repository

import ru.practicum.android.diploma.domain.models.FilterSettings

interface PrefsStorageRepository {

    fun storeFilterSettings(data: FilterSettings)

    fun getFilterSettings(): FilterSettings?
    fun clearFilterSettings()
}
