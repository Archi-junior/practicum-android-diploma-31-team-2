package ru.practicum.android.diploma.data.network

sealed interface ApiRequest {

    data object Areas : ApiRequest

    data object Industries : ApiRequest

    data class Vacancies(
        val areaId: Int? = null,
        val industryId: Int? = null,
        val text: String? = null,
        val salaryVal: Int? = null,
        val page: Int = 1,
        val onlyWithSalary: Boolean? = null
    ) : ApiRequest

    data class VacancyDetails(val id: String) : ApiRequest
}
