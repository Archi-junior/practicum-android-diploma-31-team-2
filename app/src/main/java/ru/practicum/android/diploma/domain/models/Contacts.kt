package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contacts(
    val id: String,
    val name: String,
    val email: String,
    val phones: List<Phone>
) : Parcelable
