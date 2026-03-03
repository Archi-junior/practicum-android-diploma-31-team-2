package ru.practicum.android.diploma.ui.search

import ru.practicum.android.diploma.domain.models.Vacancy

sealed interface SearchState {

    data object Loading : SearchState

    data class Content(
        val vacancies: List<Vacancy>,
        val totalFound: Int,
        val currentPage: Int,
        val isPaging: Boolean
    ) : SearchState

    data class Error(
        val message: String? = null
    ) : SearchState

    data object NoConnection : SearchState

    data object Empty : SearchState

    data object Start : SearchState
}
