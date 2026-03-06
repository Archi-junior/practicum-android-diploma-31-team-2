package ru.practicum.android.diploma.ui.branch

import ru.practicum.android.diploma.domain.models.Industry

sealed interface IndustryAction {

    data class IndustrySearchTextChange(
        val searchText: String
    ): IndustryAction

    data object IndustrySearchTextClear: IndustryAction

    data class IndustryChoose(
        val industry: Industry
    ): IndustryAction

}
