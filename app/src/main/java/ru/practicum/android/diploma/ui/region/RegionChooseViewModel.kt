package ru.practicum.android.diploma.ui.region

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.AreaInteractor
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.presentation.mapper.AreaUi
import ru.practicum.android.diploma.presentation.mapper.toAreaUiModel

@OptIn(FlowPreview::class)
class RegionChooseViewModel(
    private val areaInteractor: AreaInteractor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _regions = MutableStateFlow<List<AreaUi>>(emptyList())
    val regions: StateFlow<List<AreaUi>> = _regions

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedRegion = MutableStateFlow<AreaUi?>(null)
    val selectedRegion: StateFlow<AreaUi?> = _selectedRegion

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadRegions(countryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            areaInteractor.getRegionsByCountry(countryId).collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _regions.value = result.data.map { it.toAreaUiModel() }
                        _error.value = null
                    }
                    is ResultHttp.Error -> {
                        _error.value = result.message ?: "Ошибка загрузки регионов"
                    }
                    is ResultHttp.NoConnection -> {
                        _error.value = "Нет подключения к интернету"
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectRegion(region: AreaUi) {
        _selectedRegion.value = region
    }

    fun clearSelection() {
        _selectedRegion.value = null
    }
}
