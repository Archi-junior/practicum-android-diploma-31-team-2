package ru.practicum.android.diploma.ui.vacancy

import ru.practicum.android.diploma.domain.models.Vacancy

sealed interface VacancyState {

    object Loading: VacancyState
    object NotFound: VacancyState

    data class Content(
        val vacancy: Vacancy,
        val isFavorite: Boolean,
        val onlyFavoriteChanged: Boolean,
    ) : VacancyState

    data class Error(
        val message: String
    ) : VacancyState
}
