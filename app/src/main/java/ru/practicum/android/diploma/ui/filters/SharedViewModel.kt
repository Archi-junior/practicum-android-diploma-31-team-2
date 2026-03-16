package ru.practicum.android.diploma.ui.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.AreaInteractor
import ru.practicum.android.diploma.domain.IndustriesInteractor
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.FilterSettings
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.presentation.mapper.AreaUi
import ru.practicum.android.diploma.presentation.mapper.toAreaUiModel
import ru.practicum.android.diploma.ui.industry.IndustryAction
import ru.practicum.android.diploma.ui.industry.IndustryChooseState
import ru.practicum.android.diploma.ui.country.CountryAction
import ru.practicum.android.diploma.ui.country.CountryChooseState
import ru.practicum.android.diploma.ui.region.RegionAction
import ru.practicum.android.diploma.ui.region.RegionChooseState
import ru.practicum.android.diploma.ui.work.WorkAction
import ru.practicum.android.diploma.ui.work.WorkActionHandler
import ru.practicum.android.diploma.ui.work.WorkChooseState

class SharedViewModel(
    private val areaInteractor: AreaInteractor,
    private val industryInteractor: IndustriesInteractor,
    settingsInteractor: SettingsInteractor,
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _regions = MutableStateFlow<List<AreaUi>>(emptyList())
    val regions: StateFlow<List<AreaUi>> = _regions

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var workActionHandler: WorkActionHandler = WorkActionHandler(
        _workChooseStateLiveData, _filtersStateLiveData
    )
    private var filtersActionHandler: FiltersActionHandler

    private val _allAreas = MutableStateFlow<List<Area>>(emptyList())

    init {
        filtersActionHandler = FiltersActionHandler(
            _filtersStateLiveData,
            _workChooseStateLiveData,
            settingsInteractor
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

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
                val updatedState = when (val currentState = workChooseStateLiveData.value) {
                    is WorkChooseState.Content -> currentState.copy(country = action.country)
                    else -> WorkChooseState.Content(
                        country = action.country,
                        region = null,
                        isCountrySelected = true,
                        isRegionSelected = false
                    )
                }
                _workChooseStateLiveData.postValue(updatedState)
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
                val updatedWorkState = when (val currentWorkState = workChooseStateLiveData.value) {
                    is WorkChooseState.Content -> currentWorkState.copy(region = action.region)
                    else -> WorkChooseState.Content(
                        country = null,
                        region = action.region,
                        isCountrySelected = false,
                        isRegionSelected = true
                    )
                }
                _workChooseStateLiveData.postValue(updatedWorkState)
            }
        }
    }

    fun workOnAction(action: WorkAction) {
        when (action) {
            is WorkAction.WorkCountrySelect -> workActionHandler.handleWorkCountrySelect(action)
            is WorkAction.WorkRegionSelect -> workActionHandler.handleWorkRegionSelect(action)
            is WorkAction.WorkCountryClear -> workActionHandler.handleWorkCountryClear()
            is WorkAction.WorkRegionClear -> workActionHandler.handleWorkRegionClear()
            is WorkAction.WorkChoose -> workActionHandler.handleWorkChoose(action)
            is WorkAction.WorkRegionChange -> onWorkCountryChange()
            else -> {}
        }
    }

    fun filtersOnAction(action: FiltersAction) {
        when (action) {
            is FiltersAction.FiltersWorkChange -> filtersActionHandler.handleFiltersWorkChange()
            is FiltersAction.FiltersWorkClear -> filtersActionHandler.handleFiltersWorkClear()
            is FiltersAction.FiltersIndustryChange -> onFiltersIndustryChange()
            is FiltersAction.FiltersIndustryClear -> filtersActionHandler.handleFiltersIndustryClear()
            is FiltersAction.FiltersSalaryChange -> {
                filtersActionHandler.handleFiltersSalaryChange(action.salary)

            }
            is FiltersAction.FiltersSalaryClear -> {
                filtersActionHandler.handleFiltersSalaryClear()

            }
            is FiltersAction.FiltersOnlyWithSalaryChange -> {
                filtersActionHandler.handleFiltersOnlyWithSalaryChange(action.onlyWithSalary)

            }
            is FiltersAction.FiltersApply -> filtersActionHandler.handleFiltersApply()
            is FiltersAction.FiltersReset -> filtersActionHandler.handleFiltersReset()
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
                when (val resultHttp = industryInteractor.getIndustries().single()) {
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

    fun loadCountries() {
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

    fun loadRegions(countryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            areaInteractor.getRegionsByCountry(countryId).collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _regions.value = result.data.map { it.toAreaUiModel() }
                        _error.value = null
                    }
                    is ResultHttp.Error -> {}
                    is ResultHttp.NoConnection -> {}
                }
                _isLoading.value = false
            }
        }
    }

    fun loadAllRegions() {
        viewModelScope.launch {
            _isLoading.value = true
            areaInteractor.getAllRegions().collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _allAreas.value = result.data
                        _regions.value = result.data.map { it.toAreaUiModel() }
                        _error.value = null
                    }
                    is ResultHttp.Error -> {
                        _error.value = result.message ?: "Ошибка загрузки"
                    }
                    is ResultHttp.NoConnection -> {
                        _error.value = "Нет подключения к интернету"
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun findAndSetCountryByRegion(region: Area) {
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
                _workChooseStateLiveData.postValue(updatedState)
            } else {
                android.util.Log.d("SharedViewModel", "Country not found for region: ${region.name}")
            }
        }
    }
}
