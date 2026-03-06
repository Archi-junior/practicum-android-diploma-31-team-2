package ru.practicum.android.diploma.ui.country

sealed interface CountryAction {
    data object CountrySelectItem: CountryAction
}
