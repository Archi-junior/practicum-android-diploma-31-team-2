package ru.practicum.android.diploma.domain

import ru.practicum.android.diploma.domain.models.FakeFilterSettings

interface FakeSettingsInteractor {

    fun storeFilterSettings(data: FakeFilterSettings)

    fun getFilterSettings(): FakeFilterSettings?
}
