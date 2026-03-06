package ru.practicum.android.diploma.ui.filters

sealed interface FiltersAction {
    data object FiltersWorkChange: FiltersAction

    data object FiltersWorkClear: FiltersAction

    data object FiltersIndustryChange: FiltersAction

    data object FiltersIndustryClear: FiltersAction

    data class FiltersSalaryChange(
        val salary: Int,
    ): FiltersAction

    data object FiltersSalaryClear: FiltersAction

    data class FiltersOnlyWithSalaryChange(
        val onlyWithSalary: Boolean,
    ): FiltersAction

    data object FiltersApply: FiltersAction

    data object FiltersReset: FiltersAction

}
