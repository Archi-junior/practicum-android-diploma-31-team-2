package ru.practicum.android.diploma.ui.filters

import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Industry

sealed interface FiltersState {

    data class Content(
        val country: Area? = null,
        val region: Area? = null,
        val industry: Industry? = null,
        val salary: Int = 0,
        val onlyWithSalary: Boolean = false,
    ) : FiltersState
}
