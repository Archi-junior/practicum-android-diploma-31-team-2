package ru.practicum.android.diploma.domain.models

data class FakeFilterSettings(
    val countryId: String?,
    val countryName: String?,
    val regionId: String?,
    val regionName: String?,
    val industryId: String?,
    val industryName: String?,
    val salaryVal: Int?,
    val onlyWithSalary: Boolean = false,
)
