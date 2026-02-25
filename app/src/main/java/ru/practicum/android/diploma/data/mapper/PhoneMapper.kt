package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.PhoneDto
import ru.practicum.android.diploma.domain.models.Phone

fun PhoneDto.toDomain(): Phone =
    Phone(
        comment = comment,
        formatted = formatted,
    )
