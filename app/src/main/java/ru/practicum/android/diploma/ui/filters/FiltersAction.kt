package ru.practicum.android.diploma.ui.filters

sealed interface FiltersAction {
    data object FiltersWorkChange: FiltersAction
    data object FiltersWorkClear: FiltersAction
    data object FiltersIndustryChange: FiltersAction
    data object FiltersIndustryClear: FiltersAction
    data object FiltersSalaryChange: FiltersAction
    data object FiltersOnlyWithSalaryChange: FiltersAction
    data object FiltersApply: FiltersAction
    data object FiltersReset: FiltersAction

}
