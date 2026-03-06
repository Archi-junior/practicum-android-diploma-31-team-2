package ru.practicum.android.diploma.ui.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.FakeAreaInteractor
import ru.practicum.android.diploma.domain.FakeIndustryInteractor
import ru.practicum.android.diploma.domain.FakeSettingsInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.FakeFilterSettings
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.ui.branch.IndustryAction
import ru.practicum.android.diploma.ui.branch.IndustryChooseState
import ru.practicum.android.diploma.ui.country.CountryAction
import ru.practicum.android.diploma.ui.country.CountryChooseState
import ru.practicum.android.diploma.ui.region.RegionAction
import ru.practicum.android.diploma.ui.region.RegionChooseState
import ru.practicum.android.diploma.ui.work.WorkAction
import ru.practicum.android.diploma.ui.work.WorkChooseState

class SharedViewModel(
    private val areaInteractor: FakeAreaInteractor,
    private val industryInteractor: FakeIndustryInteractor,
    private val settingsInteractor: FakeSettingsInteractor,
) : ViewModel() {

    private val filterSettings: FakeFilterSettings? = settingsInteractor.getFilterSettings()
    private var areas = mutableListOf<Area>()
    private var industries = mutableListOf<Industry>()

    private var workChooseStateLiveData = MutableLiveData<WorkChooseState>(WorkChooseState.Loading)
    fun observeWorkChooseState(): LiveData<WorkChooseState> = workChooseStateLiveData
    private var countryChooseStateLiveData = MutableLiveData<CountryChooseState>(CountryChooseState.Loading)
    fun observeCountryChooseState(): LiveData<CountryChooseState> = countryChooseStateLiveData
    private var regionChooseStateLiveData = MutableLiveData<RegionChooseState>(RegionChooseState.Loading)
    fun observeRegionChooseState(): LiveData<RegionChooseState> = regionChooseStateLiveData

    private var industryChooseStateLiveData = MutableLiveData<IndustryChooseState>(IndustryChooseState.Loading)
    fun observeIndustryChooseState(): LiveData<IndustryChooseState> = industryChooseStateLiveData

    private var filtersStateLiveData = MutableLiveData<FiltersState>(FiltersState.Content())
    fun observeFiltersStateLiveData(): LiveData<FiltersState> = filtersStateLiveData

    fun industryOnAction(action: IndustryAction) {
        when (action) {
            is IndustryAction.IndustrySearchTextChange -> {}
            is IndustryAction.IndustrySearchTextClear -> {}
            is IndustryAction.IndustryChoose -> {}
        }
    }

    fun regionOnAction(action: RegionAction) {
        when (action) {
            is RegionAction.RegionSearchTextChange -> {}
            is RegionAction.RegionSearchTextClear -> {}
            is RegionAction.RegionSelectItem -> {}
        }
    }

    fun countryOnAction(action: CountryAction) {
        when (action) {
            is CountryAction.CountrySelectItem -> {}
        }
    }

    fun workOnAction(action: WorkAction) {
        when (action) {
            is WorkAction.WorkRegionChange -> {}
            is WorkAction.WorkRegionClear -> {}
            is WorkAction.WorkCountryChange -> {}
            is WorkAction.WorkCountryClear -> {}
        }
    }

    fun filtersOnAction(action: FiltersAction) {
        when (action) {
            is FiltersAction.FiltersWorkChange -> {}
            is FiltersAction.FiltersWorkClear -> {}
            is FiltersAction.FiltersIndustryChange -> {}
            is FiltersAction.FiltersIndustryClear -> {}
            is FiltersAction.FiltersSalaryChange -> {}
            is FiltersAction.FiltersOnlyWithSalaryChange -> {}
            is FiltersAction.FiltersApply -> {
                (filtersStateLiveData.value as FiltersState.Content).filterSettings?.let{
                    settingsInteractor.storeFilterSettings(it)
                }
            }
            is FiltersAction.FiltersReset -> {}
        }
    }



}
