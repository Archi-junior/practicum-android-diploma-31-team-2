package ru.practicum.android.diploma.ui.region

import ru.practicum.android.diploma.domain.models.Area

sealed interface RegionChooseState {

    data object Loading : RegionChooseState

    data class Content(
        val searchText: String,
        val areas: List<Area>
    ) : RegionChooseState

    data object Empty : RegionChooseState

    data object NoConnection : RegionChooseState

    data class Error(
        val message: String? = null
    ) : RegionChooseState
}
