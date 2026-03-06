package ru.practicum.android.diploma.domain.models

data class FakeFilterSettings(
    val country: Area?,
    val region: Area?,
    val industry: Industry?,
    val salary: Int?,
    val onlyWithSalary: Boolean = false,
)
