package ru.practicum.android.diploma.domain.models

import ru.practicum.android.diploma.data.dto.AreaDto

data class Area(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: List<Area>,
)
