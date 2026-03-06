package ru.practicum.android.diploma.ui.region

sealed interface RegionChooseState {

    data object Loading : RegionChooseState

    data class Content(
        val isLoadingNextPage: Boolean = false
    ) : RegionChooseState

    data object Empty : RegionChooseState

    data object NoConnection : RegionChooseState

    data class Error(
        val message: String? = null
    ) : RegionChooseState
}
