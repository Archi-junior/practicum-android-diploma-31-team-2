package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Salary(
    val id: String,
    val from: Int?,
    val to: Int?,
    val currency: String?,
) : Parcelable
