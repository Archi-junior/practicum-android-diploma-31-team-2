package ru.practicum.android.diploma.ui.work

import androidx.lifecycle.MutableLiveData
import ru.practicum.android.diploma.ui.filters.FiltersState

class WorkActionHandler(
    private val workChooseStateLiveData: MutableLiveData<WorkChooseState>,
    private val filtersStateLiveData: MutableLiveData<FiltersState>
) {

    fun handleWorkCountrySelect(action: WorkAction.WorkCountrySelect) {
        workChooseStateLiveData.postValue(
            WorkChooseState.Content(
                country = action.country,
                region = null,
                isCountrySelected = true,
                isRegionSelected = false
            )
        )
    }

    fun handleWorkRegionSelect(action: WorkAction.WorkRegionSelect) {
        val currentState = workChooseStateLiveData.value as? WorkChooseState.Content
        workChooseStateLiveData.postValue(
            WorkChooseState.Content(
                country = currentState?.country,
                region = action.region,
                isCountrySelected = currentState?.country != null,
                isRegionSelected = true
            )
        )
    }

    fun handleWorkCountryClear() {
        workChooseStateLiveData.postValue(
            WorkChooseState.Content(
                country = null,
                region = null,
                isCountrySelected = false,
                isRegionSelected = false
            )
        )
    }

    fun handleWorkRegionClear() {
        val currentState = workChooseStateLiveData.value as? WorkChooseState.Content
        workChooseStateLiveData.postValue(
            WorkChooseState.Content(
                country = currentState?.country,
                region = null,
                isCountrySelected = currentState?.country != null,
                isRegionSelected = false
            )
        )
    }

    fun handleWorkChoose(action: WorkAction.WorkChoose) {
        filtersStateLiveData.postValue(
            (filtersStateLiveData.value as FiltersState.Content).copy(
                country = action.country,
                region = action.region
            )
        )
    }
}
