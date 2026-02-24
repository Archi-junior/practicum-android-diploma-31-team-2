package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.AreaDto
import ru.practicum.android.diploma.data.dto.IndustryDto
import ru.practicum.android.diploma.data.dto.VacancyDto

sealed class ApiResponseData {
    data class Areas(val areas: List<AreaDto>) : ApiResponseData()
    data class Industries(val industries: List<IndustryDto>) : ApiResponseData()
    data class Vacancies(
        val found: Int, val pages: Int, val page: Int, val items: List<VacancyDto>
    ) : ApiResponseData()

    data class VacancyDetails(val vacancy: VacancyDto) : ApiResponseData()
}
