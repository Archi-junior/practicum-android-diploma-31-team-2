package ru.practicum.android.diploma.ui.work

import ru.practicum.android.diploma.domain.models.Area

sealed interface WorkAction {

    data object WorkRegionChange: WorkAction

    data object WorkRegionClear: WorkAction

    data object WorkCountryChange: WorkAction

    data object WorkCountryClear: WorkAction

    data class WorkChoose(
        val country: Area?,
        val region: Area?,
    ): WorkAction

}
