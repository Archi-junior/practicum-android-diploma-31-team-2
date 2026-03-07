package ru.practicum.android.diploma.ui.region

import ru.practicum.android.diploma.domain.models.Area

sealed interface RegionAction {

    data class RegionSearchTextChange(
        val searchText: String,
    ): RegionAction

    data object RegionSearchTextClear: RegionAction

    data class RegionSelectItem(
        val region: Area
    ): RegionAction

}
