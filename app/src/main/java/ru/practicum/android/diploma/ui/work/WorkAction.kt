package ru.practicum.android.diploma.ui.work

sealed interface WorkAction {
    data object WorkRegionChange: WorkAction
    data object WorkRegionClear: WorkAction
    data object WorkCountryChange: WorkAction
    data object WorkCountryClear: WorkAction
}
