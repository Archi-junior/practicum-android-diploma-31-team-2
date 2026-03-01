package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Area(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: List<Area>,
) : Parcelable
