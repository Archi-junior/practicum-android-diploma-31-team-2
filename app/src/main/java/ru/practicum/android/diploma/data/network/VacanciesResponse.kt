package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.VacancyDto

data class VacanciesResponse(
    val found: Int,
    val pages: Int,
    val page: Int,
    val items: List<VacancyDto>
) : ApiResponse()
