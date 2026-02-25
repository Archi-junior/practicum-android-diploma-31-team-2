package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.EmploymentDto
import ru.practicum.android.diploma.domain.models.Employment

fun EmploymentDto?.toDomain(): Employment? =
    this?.let {
        Employment(
            id = it.id,
            name = it.name,
        )
    }
