package ru.practicum.android.diploma.domain.models

data class VacanciesFilter(
    val areaId: Int? = null,
    val industryId: Int? = null,
    val text: String? = null,
    val salaryVal: Int? = null,
    val page: Int = 1,
    val onlyWithSalary: Boolean? = null
)
