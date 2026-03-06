package ru.practicum.android.diploma.ui.region

sealed interface RegionAction {
    data object RegionSearchTextChange: RegionAction
    data object RegionSearchTextClear: RegionAction
    data object RegionSelectItem: RegionAction
}
