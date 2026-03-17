package ru.practicum.android.diploma.ui.region

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.AreaInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.ui.work.WorkChooseState

class RegionSelectionHandler(
    private val areaInteractor: AreaInteractor,
    private val workChooseStateLiveData: MutableLiveData<WorkChooseState>
) {

    fun findAndSetCountryByRegion(region: Area, viewModelScope: kotlinx.coroutines.CoroutineScope) {
        viewModelScope.launch {
            val country = areaInteractor.findCountryByRegion(region.id)
            if (country != null) {
                val currentState = workChooseStateLiveData.value

                val updatedState = when (currentState) {
                    is WorkChooseState.Content -> {
                        WorkChooseState.Content(
                            country = country,
                            region = region,
                            isCountrySelected = true,
                            isRegionSelected = true
                        )
                    }
                    else -> WorkChooseState.Content(
                        country = country,
                        region = region,
                        isCountrySelected = true,
                        isRegionSelected = true
                    )
                }
                workChooseStateLiveData.postValue(updatedState)
            } else {
                android.util.Log.d("RegionSelectionHandler", "Country not found for region: ${region.name}")
            }
        }
    }
}
