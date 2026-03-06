package ru.practicum.android.diploma.ui.work

sealed interface WorkChooseState {

    data object Loading : WorkChooseState

    data class Content(
        val isLoadingNextPage: Boolean = false
    ) : WorkChooseState

    data object Empty : WorkChooseState

    data object NoConnection : WorkChooseState

    data class Error(
        val message: String? = null
    ) : WorkChooseState
}
