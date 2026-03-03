package ru.practicum.android.diploma.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.SearchInteractor
import ru.practicum.android.diploma.domain.models.VacanciesFilter

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>(SearchState.Start)
    val state: LiveData<SearchState> = _state

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var currentFilter = VacanciesFilter(page = 1)

    private var isSearching = false

    init {
        _searchQuery
            .debounce(2000)
            .distinctUntilChanged()
            .filter { query ->
                if (query.isBlank()) {
                    _state.postValue(SearchState.Start)
                    false
                } else {
                    true
                }
            }
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _state.value = SearchState.Start
    }

    fun retrySearch() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState is SearchState.Content &&
            !currentState.isPaging &&
            currentState.vacancies.isNotEmpty() &&
            !isSearching) {

            currentFilter = currentFilter.copy(page = currentFilter.page + 1)
            performSearch(_searchQuery.value, isPaging = true)
        }
    }

    private fun performSearch(query: String, isPaging: Boolean = false) {
        if (isSearching) return

        viewModelScope.launch {
            isSearching = true

            if (!isPaging) {
                currentFilter = VacanciesFilter(
                    text = query,
                    page = 1
                )
                _state.value = SearchState.Loading
            } else {
                val currentList = (_state.value as? SearchState.Content)?.vacancies ?: emptyList()
                _state.value = SearchState.Content(
                    vacancies = currentList,
                    totalFound = (_state.value as? SearchState.Content)?.totalFound ?: 0,
                    currentPage = (_state.value as? SearchState.Content)?.currentPage ?: 1,
                    isPaging = true
                )
            }

            searchInteractor.searchVacancies(currentFilter)
                .collectLatest { result ->
                    when (result) {
                        is ResultHttp.Success -> {
                            val newVacancies = result.data

                            if (newVacancies.isEmpty()) {
                                if (isPaging) {
                                    _state.value = _state.value
                                } else {
                                    _state.value = SearchState.Empty
                                }
                            } else {
                                val currentVacancies = if (isPaging) {
                                    val oldState = _state.value as? SearchState.Content
                                    oldState?.vacancies?.plus(newVacancies) ?: newVacancies
                                } else {
                                    newVacancies
                                }

                                _state.value = SearchState.Content(
                                    vacancies = currentVacancies,
                                    totalFound = result.data.size,
                                    currentPage = currentFilter.page,
                                    isPaging = false
                                )
                            }
                        }

                        is ResultHttp.Error -> {
                            if (isPaging) {
                                _state.value = _state.value
                            } else {
                                _state.value = SearchState.Error(result.message)
                            }
                        }

                        is ResultHttp.NoConnection -> {
                            _state.value = SearchState.NoConnection
                        }
                    }
                    isSearching = false
                }
        }
    }
}
