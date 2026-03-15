package ru.practicum.android.diploma.domain

import ru.practicum.android.diploma.domain.models.FilterSettings

interface SettingsInteractor {

    fun storeFilterSettings(data: FilterSettings)

    fun getFilterSettings(): FilterSettings?
    fun clearFilterSettings()
}
