package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.EmployerDto
import ru.practicum.android.diploma.domain.models.Employer

fun EmployerDto.toDomain(): Employer =
    Employer(
        id = id,
        name = name,
        logo = logo,
    )
