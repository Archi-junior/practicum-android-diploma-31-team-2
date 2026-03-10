package ru.practicum.android.diploma.ui.work

import ru.practicum.android.diploma.domain.models.Area

sealed interface WorkAction {

    data object WorkCountryClick: WorkAction

    data object WorkRegionClick: WorkAction

    data class WorkCountrySelect(val country: Area): WorkAction

    data class WorkRegionSelect(val region: Area): WorkAction

    data object WorkCountryClear: WorkAction

    data object WorkRegionClear: WorkAction

    data class WorkChoose(
        val country: Area?,
        val region: Area?
    ): WorkAction
}
