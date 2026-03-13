package ru.practicum.android.diploma.ui.work

import ru.practicum.android.diploma.domain.models.Area

sealed interface WorkChooseState {

    data object Initial : WorkChooseState

    data object Loading : WorkChooseState

    data class Content(
        val country: Area? = null,
        val region: Area? = null,
        val isCountrySelected: Boolean = false,
        val isRegionSelected: Boolean = false
    ) : WorkChooseState

    data object Empty : WorkChooseState

    data object NoConnection : WorkChooseState

    data class Error(
        val message: String? = null
    ) : WorkChooseState
}
