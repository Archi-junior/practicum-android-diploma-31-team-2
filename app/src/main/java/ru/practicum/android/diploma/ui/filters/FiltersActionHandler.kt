package ru.practicum.android.diploma.ui.filters

import androidx.lifecycle.MutableLiveData
import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.models.FilterSettings
import ru.practicum.android.diploma.ui.work.WorkChooseState

class FiltersActionHandler(
    private val filtersStateLiveData: MutableLiveData<FiltersState>,
    private val workChooseStateLiveData: MutableLiveData<WorkChooseState>,
    private val settingsInteractor: SettingsInteractor,
) {
    fun handleFiltersWorkChange() {
        val filterData = filtersStateLiveData.value as FiltersState.Content
        workChooseStateLiveData.postValue(
            WorkChooseState.Content(country = filterData.country, region = filterData.region)
        )
    }

    fun handleFiltersWorkClear() {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(country = null, region = null)
        )
    }

    fun handleFiltersIndustryClear() {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(industry = null)
        )
    }

    fun handleFiltersSalaryChange(salary: Int) {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(salary = salary)
        )
    }

    fun handleFiltersSalaryClear() {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(salary = 0)
        )
    }

    fun handleFiltersOnlyWithSalaryChange(onlyWithSalary: Boolean) {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(onlyWithSalary = onlyWithSalary)
        )
    }

    fun handleFiltersApply() {
        val filterData = filtersStateLiveData.value as FiltersState.Content
        settingsInteractor.storeFilterSettings(
            FilterSettings(
                country = filterData.country,
                region = filterData.region,
                industry = filterData.industry,
                salary = filterData.salary,
                onlyWithSalary = filterData.onlyWithSalary,
            )
        )
    }

    fun handleFiltersReset() {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(
                country = null,
                region = null,
                industry = null,
                salary = 0,
                onlyWithSalary = false,
            )
        )
    }
}
