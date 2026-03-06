package ru.practicum.android.diploma.domain.models

data class FilterSettings(
    val countryId: String?,
    val countryName: String?,
    val regionId: String?,
    val regionName: String?,
    val industryId: String?,
    val industryName: String?,
    val salary: Int = 0,
    val onlyWithSalary: Boolean = false,
)
