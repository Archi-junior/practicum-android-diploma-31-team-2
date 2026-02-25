package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.ScheduleDto
import ru.practicum.android.diploma.domain.models.Schedule

fun ScheduleDto?.toDomain(): Schedule? =
    this?.let {
        Schedule(
            id = it.id,
            name = it.name,
        )
    }
