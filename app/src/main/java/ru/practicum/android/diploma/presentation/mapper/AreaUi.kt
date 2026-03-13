package ru.practicum.android.diploma.presentation.mapper

import ru.practicum.android.diploma.domain.models.Area

data class AreaUi(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: List<AreaUi>
)

fun Area.toAreaUiModel(): AreaUi {
    return AreaUi(
        id = id,
        name = name,
        parentId = parentId,
        areas = areas.map { it.toAreaUiModel() }
    )
}

fun AreaUi.toDomainArea(): Area {
    return Area(
        id = id,
        name = name,
        parentId = parentId,
        areas = areas.map { it.toDomainArea() }
    )
}
