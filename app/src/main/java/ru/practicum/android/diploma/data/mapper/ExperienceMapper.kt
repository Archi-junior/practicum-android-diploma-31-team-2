package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.ExperienceDto
import ru.practicum.android.diploma.domain.models.Experience

fun ExperienceDto?.toDomain(): Experience? =
    this?.let {
        Experience(
            id = it.id,
            name= it.name,
        )
    }
