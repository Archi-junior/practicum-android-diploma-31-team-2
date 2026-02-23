package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.AreaDto

data class AreasResponse(
    val areas: List<AreaDto>
) : ApiResponse()
