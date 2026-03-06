package ru.practicum.android.diploma.ui.filters

import ru.practicum.android.diploma.domain.models.FakeFilterSettings

sealed interface FiltersState {

    data class Content(
        val filterSettings: FakeFilterSettings? = null
    ) : FiltersState
}
