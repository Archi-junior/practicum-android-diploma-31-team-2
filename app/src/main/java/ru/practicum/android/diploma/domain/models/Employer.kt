package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Employer(
    val id: String,
    val name: String,
    val logo: String,
) : Parcelable
