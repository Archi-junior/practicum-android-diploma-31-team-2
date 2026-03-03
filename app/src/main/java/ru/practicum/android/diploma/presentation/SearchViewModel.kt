package ru.practicum.android.diploma.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactor.StatusPlaceholderInteractor
import ru.practicum.android.diploma.util.SearchState
import ru.practicum.android.diploma.util.UiState

class SearchViewModel(
    private val statusPlaceholderInteractor: StatusPlaceholderInteractor
) : ViewModel() {

    val _uiState = MutableStateFlow<UiState<List<Vacancy>>>(UiState.Success(emptyList()))
    val uiState: StateFlow<UiState<List<Vacancy>>> = _uiState.asStateFlow()

    private val searchState = MutableStateFlow(SearchState.SUCCESS)
    val searchObserver: StateFlow<SearchState> = searchState.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            statusPlaceholderInteractor.searchVacancies(
                mapOf(
                    "query" to query,
                    "limit" to "20"
                )
            ).collect { result ->
                searchState.value = when (result) {
                    is UiState.Success<*> -> SearchState.SUCCESS
                    is UiState.Error -> SearchState.ERROR
                    is UiState.NoConnection -> SearchState.NO_CONNECTION
                    is UiState.Empty -> SearchState.EMPTY
                    else -> {}
                } as SearchState
            }
        }
    }
}
