package ru.practicum.android.diploma.ui.search

import ru.practicum.android.diploma.domain.models.Vacancy

sealed interface SearchState {

    data object Start : SearchState

    data object Loading : SearchState

    data class Content(
        val vacancies: List<Vacancy>,
        val totalFound: Int,
        val currentPage: Int,
        val totalPages: Int,
        val isLoadingNextPage: Boolean = false
    ) : SearchState

    data object Empty : SearchState

    data object NoConnection : SearchState

    data class Error(
        val message: String? = null
    ) : SearchState

    data class PaginationError(
        val message: String? = null,
        val showToast: Boolean = true
    ) : SearchState

    data class PaginationNoConnection(
        val showToast: Boolean = true
    ) : SearchState
}
