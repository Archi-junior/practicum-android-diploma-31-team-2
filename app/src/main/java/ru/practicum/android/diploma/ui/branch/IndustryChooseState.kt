package ru.practicum.android.diploma.ui.branch

sealed interface IndustryChooseState {

    data object Loading : IndustryChooseState

    data class Content(
        val isLoadingNextPage: Boolean = false
    ) : IndustryChooseState

    data object Empty : IndustryChooseState

    data object NoConnection : IndustryChooseState

    data class Error(
        val message: String? = null
    ) : IndustryChooseState
}
