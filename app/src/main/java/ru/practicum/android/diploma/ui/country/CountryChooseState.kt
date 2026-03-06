package ru.practicum.android.diploma.ui.country

import ru.practicum.android.diploma.domain.models.Area

sealed interface CountryChooseState {

    data object Loading : CountryChooseState

    data class Content(
        val areas: List<Area>
    ) : CountryChooseState

    data object NoConnection : CountryChooseState

    data class Error(
        val message: String? = null
    ) : CountryChooseState
}
