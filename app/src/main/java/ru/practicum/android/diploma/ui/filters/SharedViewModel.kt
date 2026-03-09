package ru.practicum.android.diploma.ui.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.AreaInteractor
import ru.practicum.android.diploma.domain.FakeIndustryInteractor
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.FilterSettings
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
    private val areaInteractor: AreaInteractor,
    private val industryInteractor: FakeIndustryInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val filterSettings = settingsInteractor.getFilterSettings()
    private var areas = mutableListOf<Area>()
    private var industries = mutableListOf<Industry>()

    private val _industryChooseStateLiveData = MutableLiveData<IndustryChooseState>(IndustryChooseState.Initial)
    val industryChooseStateLiveData: LiveData<IndustryChooseState> = _industryChooseStateLiveData
    private val _countryChooseStateLiveData = MutableLiveData<CountryChooseState>(CountryChooseState.Initial)
    val countryChooseStateLiveData: LiveData<CountryChooseState> = _countryChooseStateLiveData
    private val _regionChooseStateLiveData = MutableLiveData<RegionChooseState>(RegionChooseState.Initial)
    val regionChooseStateLiveData: LiveData<RegionChooseState> = _regionChooseStateLiveData
    private val _workChooseStateLiveData = MutableLiveData<WorkChooseState>(WorkChooseState.Initial)
    val workChooseStateLiveData: LiveData<WorkChooseState> = _workChooseStateLiveData
    private val _filtersStateLiveData = MutableLiveData<FiltersState>(
        FiltersState.Content(
            country = filterSettings?.country,
            region = filterSettings?.region,
            industry = filterSettings?.industry,
            salary = filterSettings?.salary ?: 0,
            onlyWithSalary = filterSettings?.onlyWithSalary ?: false,
        )
    )
    val filtersStateLiveData: LiveData<FiltersState> = _filtersStateLiveData

    fun industryOnAction(action: IndustryAction) {
        when (action) {
            is IndustryAction.IndustrySearchTextChange -> {
                _industryChooseStateLiveData.postValue(
                    (industryChooseStateLiveData.value as IndustryChooseState.Content).copy(
                        searchText = action.searchText,
                    )
                )
            }
            is IndustryAction.IndustrySearchTextClear -> {
                _industryChooseStateLiveData.postValue(
                    (industryChooseStateLiveData.value as IndustryChooseState.Content).copy(
                        searchText = ""
                    )
                )
            }
            is IndustryAction.IndustryChoose -> {
                _filtersStateLiveData.postValue(
                    (filtersStateLiveData.value as FiltersState.Content).copy(
                        industry = action.industry
                    )
                )
            }
        }
    }

    fun countryOnAction(action: CountryAction) {
        when (action) {
            is CountryAction.CountrySelectItem -> {
                _workChooseStateLiveData.postValue(
                    (workChooseStateLiveData.value as WorkChooseState.Content).copy(
                        country = action.country
                    )
                )
            }
        }
    }

    fun regionOnAction(action: RegionAction) {
        when (action) {
            is RegionAction.RegionSearchTextChange -> {
                _regionChooseStateLiveData.postValue(
                    (regionChooseStateLiveData.value as RegionChooseState.Content).copy(
                        searchText = action.searchText,
                    )
                )
            }
            is RegionAction.RegionSearchTextClear -> {
                _regionChooseStateLiveData.postValue(
                    (regionChooseStateLiveData.value as RegionChooseState.Content).copy(
                        searchText = ""
                    )
                )
            }
            is RegionAction.RegionSelectItem -> {
                _workChooseStateLiveData.postValue(
                    (workChooseStateLiveData.value as WorkChooseState.Content).copy(
                        region = action.region
                    )
                )
            }
        }
    }

    fun workOnAction(action: WorkAction) {
        when (action) {
            is WorkAction.WorkRegionChange -> {
                val country = (workChooseStateLiveData.value as WorkChooseState.Content).country
                _regionChooseStateLiveData.postValue(
                    if (country == null) {
                        RegionChooseState.Empty
                    } else {
                        RegionChooseState.Content(
                            searchText = "",
                            areas = country.areas,
                        )
                    }
                )
            }
            is WorkAction.WorkRegionClear -> {
                _workChooseStateLiveData.postValue(
                    (workChooseStateLiveData.value as WorkChooseState.Content).copy(
                        region = null
                    )
                )
            }
            is WorkAction.WorkCountryChange -> onWorkCountryChange()
            is WorkAction.WorkCountryClear -> {
                _workChooseStateLiveData.postValue(
                    (workChooseStateLiveData.value as WorkChooseState.Content).copy(
                        country = null
                    )
                )
            }
            is WorkAction.WorkChoose -> {
                _filtersStateLiveData.postValue(
                    (filtersStateLiveData.value as FiltersState.Content).copy(
                        country = action.country,
                        region = action.region,
                    )
                )
            }
        }
    }

    fun filtersOnAction(action: FiltersAction) {
        when (action) {
            is FiltersAction.FiltersWorkChange -> {
                val filterData = filtersStateLiveData.value as FiltersState.Content
                _workChooseStateLiveData.postValue(
                    WorkChooseState.Content(country = filterData.country, region = filterData.region)
                )
            }
            is FiltersAction.FiltersWorkClear -> {
                _filtersStateLiveData.postValue(
                    (filtersStateLiveData.value as FiltersState.Content).copy(country = null, region = null)
                )
            }
            is FiltersAction.FiltersIndustryChange -> onFiltersIndustryChange()
            is FiltersAction.FiltersIndustryClear -> {
                _filtersStateLiveData.postValue(
                    (filtersStateLiveData.value as FiltersState.Content).copy(industry = null)
                )
            }
            is FiltersAction.FiltersSalaryChange -> {
                _filtersStateLiveData.postValue(
                    (filtersStateLiveData.value as FiltersState.Content).copy(salary = action.salary)
                )
            }
            is FiltersAction.FiltersSalaryClear -> {
                _filtersStateLiveData.postValue((filtersStateLiveData.value as FiltersState.Content).copy(salary = 0))
            }
            is FiltersAction.FiltersOnlyWithSalaryChange -> {
                _filtersStateLiveData.postValue(
                    (filtersStateLiveData.value as FiltersState.Content).copy(onlyWithSalary = action.onlyWithSalary)
                )
            }
            is FiltersAction.FiltersApply -> {
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
            is FiltersAction.FiltersReset -> {
                _filtersStateLiveData.postValue(
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
    }

    private fun onWorkCountryChange() {
        if (areas.isEmpty()) {
            viewModelScope.launch {
                _countryChooseStateLiveData.postValue(CountryChooseState.Loading)
                when (val resultHttp = areaInteractor.getCountries().single()) {
                    is ResultHttp.Success -> {
                        areas.addAll(resultHttp.data)
                        _countryChooseStateLiveData.postValue(
                            CountryChooseState.Content(
                                areas = resultHttp.data,
                            )
                        )
                    }
                    is ResultHttp.Error -> {
                        _countryChooseStateLiveData.postValue(
                            CountryChooseState.Error(resultHttp.message.toString())
                        )
                    }
                    is ResultHttp.NoConnection -> {
                        _countryChooseStateLiveData.postValue(CountryChooseState.NoConnection)
                    }
                }
            }
        } else {
            _countryChooseStateLiveData.postValue(
                CountryChooseState.Content(
                    areas = areas,
                )
            )
        }
    }

    private fun onFiltersIndustryChange() {
        if (industries.isEmpty()) {
            viewModelScope.launch {
                _industryChooseStateLiveData.postValue(IndustryChooseState.Loading)
                when (val resultHttp = industryInteractor.getAll().single()) {
                    is ResultHttp.Success -> {
                        industries.addAll(resultHttp.data)
                        _industryChooseStateLiveData.postValue(
                            IndustryChooseState.Content(
                                searchText = "",
                                industries = industries,
                            )
                        )
                    }
                    is ResultHttp.Error -> {
                        _industryChooseStateLiveData.postValue(
                            IndustryChooseState.Error(resultHttp.message.toString())
                        )
                    }
                    is ResultHttp.NoConnection -> {
                        _industryChooseStateLiveData.postValue(IndustryChooseState.NoConnection)
                    }
                }
            }
        } else {
            _industryChooseStateLiveData.postValue(
                IndustryChooseState.Content(
                    searchText = "",
                    industries = industries,
                )
            )
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _countryChooseStateLiveData.postValue(CountryChooseState.Loading)

            areaInteractor.getCountries().collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _countryChooseStateLiveData.postValue(
                            CountryChooseState.Content(areas = result.data)
                        )
                    }
                    is ResultHttp.Error -> {
                        _countryChooseStateLiveData.postValue(
                            CountryChooseState.Error(result.message.toString())
                        )
                    }
                    is ResultHttp.NoConnection -> {
                        _countryChooseStateLiveData.postValue(CountryChooseState.NoConnection)
                    }
                }
            }
        }
    }

    private fun loadRegions(countryId: Int) {
        viewModelScope.launch {
            _regionChooseStateLiveData.postValue(RegionChooseState.Loading)

            areaInteractor.getRegionsByCountry(countryId).collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _regionChooseStateLiveData.postValue(
                            RegionChooseState.Content(
                                searchText = "",
                                areas = result.data
                            )
                        )
                    }
                    is ResultHttp.Error -> {
                        _regionChooseStateLiveData.postValue(
                            RegionChooseState.Error(result.message.toString())
                        )
                    }
                    is ResultHttp.NoConnection -> {
                        _regionChooseStateLiveData.postValue(RegionChooseState.NoConnection)
                    }
                }
            }
        }
    }
}
