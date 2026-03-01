package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val city: String,
    val street: String,
    val building: String,
    val fullAddress: String,
) : Parcelable
