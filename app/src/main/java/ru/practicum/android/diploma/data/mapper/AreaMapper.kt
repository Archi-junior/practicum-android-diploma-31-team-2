package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.AreaDto
import ru.practicum.android.diploma.domain.models.Area

fun AreaDto.toDomain(): Area =
    Area(
        id = id,
        name = name,
        parentId = parentId,
        areas = areas.map { it.toDomain() }
    )
