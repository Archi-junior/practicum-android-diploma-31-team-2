package ru.practicum.android.diploma.ui.region

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.presentation.mapper.AreaUi

@OptIn(FlowPreview::class)
class RegionChooseViewModel(val fakeAreaInteractor: FakeAreaInteractor) : ViewModel() {

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

    init {
        loadRegions()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadRegions() {
        viewModelScope.launch {
            try {
                val regions = fakeAreaInteractor.getAll()
                _regions.value = regions
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки регионов"
            }
        }
    }

    fun selectRegion(region: AreaUi) {
        _selectedRegion.value = region
    }

    fun getSelectedRegion(): AreaUi? = _selectedRegion.value
}
