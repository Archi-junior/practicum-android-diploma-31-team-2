package ru.practicum.android.diploma.ui.branch

sealed interface IndustryAction {
    data object IndustrySearchTextChange: IndustryAction
    data object IndustrySearchTextClear: IndustryAction
    data object IndustryChoose: IndustryAction
}
