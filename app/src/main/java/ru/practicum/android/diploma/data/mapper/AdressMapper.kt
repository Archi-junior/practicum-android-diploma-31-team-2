package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.AddressDto
import ru.practicum.android.diploma.domain.models.Address

fun AddressDto?.toDomain(): Address? =
    this?.let {
        Address(
            city = it.city,
            street = it.street,
            building = it.building,
            fullAddress = it.raw
        )
    }
