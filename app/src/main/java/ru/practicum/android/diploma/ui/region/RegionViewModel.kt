package ru.practicum.android.diploma.ui.region

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.AreaInteractor
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.presentation.mapper.AreaUi
import ru.practicum.android.diploma.presentation.mapper.toAreaUiModel

class RegionViewModel(
    private val areaInteractor: AreaInteractor
) : ViewModel() {

    private val _regions = MutableStateFlow<List<AreaUi>>(emptyList())
    val regions: StateFlow<List<AreaUi>> = _regions

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _allAreas = MutableStateFlow<List<Area>>(emptyList())

    fun loadRegions(countryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            areaInteractor.getRegionsByCountry(countryId).collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _regions.value = result.data.map { it.toAreaUiModel() }
                        _error.value = null
                    }
                    is ResultHttp.Error, is ResultHttp.NoConnection -> {
                    }
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
}
