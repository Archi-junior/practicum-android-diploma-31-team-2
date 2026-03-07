package ru.practicum.android.diploma.domain.models

data class FilterSettings(
    val country: Area?,
    val region: Area?,
    val industry: Industry?,
    val salary: Int = 0,
    val onlyWithSalary: Boolean = false,
)
