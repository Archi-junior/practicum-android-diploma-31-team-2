package ru.practicum.android.diploma.ui.branch

import ru.practicum.android.diploma.domain.models.Industry

sealed interface IndustryChooseState {

    data object Loading : IndustryChooseState

    data class Content(
        val searchText: String,
        val industries: List<Industry>,
    ) : IndustryChooseState

    data object NoConnection : IndustryChooseState

    data class Error(
        val message: String? = null
    ) : IndustryChooseState
}
