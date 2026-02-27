package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.ContactsDto
import ru.practicum.android.diploma.domain.models.Contacts

fun ContactsDto?.toDomain(): Contacts? =
    this?.let {
        Contacts(
            id = it.id,
            name = it.name,
            email = it.email,
            phones = it.phones.map { child -> child.toDomain() }
        )
    }
