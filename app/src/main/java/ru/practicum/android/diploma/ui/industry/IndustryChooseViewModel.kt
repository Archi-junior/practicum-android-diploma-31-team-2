package ru.practicum.android.diploma.ui.industry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.IndustriesInteractor
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.models.Industry

@OptIn(FlowPreview::class)
class IndustryChooseViewModel(
    private val industriesInteractor: IndustriesInteractor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allIndustries = MutableStateFlow<List<Industry>>(emptyList())
    private val _filteredIndustries = MutableStateFlow<List<Industry>>(emptyList())
    val filteredIndustries: StateFlow<List<Industry>> = _filteredIndustries

    private val _selectedIndustry = MutableStateFlow<Industry?>(null)
    val selectedIndustry: StateFlow<Industry?> = _selectedIndustry

    private val _state = MutableStateFlow<IndustryChooseState>(IndustryChooseState.Loading)
    val state: StateFlow<IndustryChooseState> = _state

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadIndustries()

        viewModelScope.launch {
            combine(
                _allIndustries,
                _searchQuery.debounce(300).distinctUntilChanged()
            ) { industries, query ->
                if (query.isBlank()) {
                    industries
                } else {
                    industries.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filtered ->
                _filteredIndustries.value = filtered
                updateState()
            }
        }
    }

    fun loadIndustries() {
        viewModelScope.launch {
            _isLoading.value = true
            _state.value = IndustryChooseState.Loading

            industriesInteractor.getIndustries().collect { result ->
                when (result) {
                    is ResultHttp.Success -> {
                        _allIndustries.value = result.data
                        _filteredIndustries.value = result.data
                        _error.value = null
                        _isLoading.value = false
                        updateState()
                    }
                    is ResultHttp.Error -> {
                        _error.value = result.message ?: "Ошибка загрузки"
                        _state.value = IndustryChooseState.Error(result.message)
                        _isLoading.value = false
                    }
                    is ResultHttp.NoConnection -> {
                        _state.value = IndustryChooseState.NoConnection
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onIndustrySelected(industry: Industry) {
        _selectedIndustry.value = industry
        updateState()
    }

    fun clearSelection() {
        _selectedIndustry.value = null
        updateState()
    }

    private fun updateState() {
        _state.value = when {
            _isLoading.value -> IndustryChooseState.Loading
            _error.value != null -> IndustryChooseState.Error(_error.value)
            _filteredIndustries.value.isEmpty() && _searchQuery.value.isNotBlank() -> IndustryChooseState.Content(
                searchText = _searchQuery.value,
                industries = emptyList()
            )
            _allIndustries.value.isEmpty() && !_isLoading.value -> IndustryChooseState.Content(
                searchText = _searchQuery.value,
                industries = emptyList()
            )
            else -> IndustryChooseState.Content(
                searchText = _searchQuery.value,
                industries = _filteredIndustries.value
            )
        }
    }
}
