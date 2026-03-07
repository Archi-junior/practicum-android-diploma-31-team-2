package ru.practicum.android.diploma.ui.favorite

import ru.practicum.android.diploma.domain.models.Vacancy

sealed interface FavoriteState {

    object Loading: FavoriteState

    data class Content(
        val vacancies: List<Vacancy>,
    ) : FavoriteState

    data class Error(
        val message: String
    ) : FavoriteState
}
