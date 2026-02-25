package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.SalaryDto
import ru.practicum.android.diploma.domain.models.Salary

fun SalaryDto?.toDomain(): Salary? =
    this?.let {
        Salary(
            id = it.id,
            from = it.from,
            to = it.to,
            currency = it.currency,
        )
    }
