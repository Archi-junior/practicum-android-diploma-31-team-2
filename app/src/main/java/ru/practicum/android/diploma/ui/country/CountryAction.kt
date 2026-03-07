package ru.practicum.android.diploma.ui.country

import ru.practicum.android.diploma.domain.models.Area

sealed interface CountryAction {

    data class CountrySelectItem(
        val country: Area
    ): CountryAction

}
