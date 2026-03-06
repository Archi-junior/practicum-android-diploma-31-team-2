package ru.practicum.android.diploma.ui.country

sealed interface CountryChooseState {

    data object Loading : CountryChooseState

    data class Content(
        val isLoadingNextPage: Boolean = false
    ) : CountryChooseState

    data object Empty : CountryChooseState

    data object NoConnection : CountryChooseState

    data class Error(
        val message: String? = null
    ) : CountryChooseState
}
