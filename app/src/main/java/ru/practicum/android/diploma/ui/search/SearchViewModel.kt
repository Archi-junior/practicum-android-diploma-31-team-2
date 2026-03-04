package ru.practicum.android.diploma.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.models.VacanciesFilter

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val vacanciesInteractor: VacanciesInteractor
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

    private fun performSearch(query: String) {
        if (isSearching) return

        viewModelScope.launch {
            isSearching = true
            _state.value = SearchState.Loading

            currentFilter = VacanciesFilter(
                text = query,
                page = 1
            )

            vacanciesInteractor.search(currentFilter)
                .collectLatest { result ->
                    when (result) {
                        is ResultHttp.Success -> {
                            if (result.data.isEmpty()) {
                                _state.value = SearchState.Empty
                            } else {
                                _state.value = SearchState.Content(
                                    vacancies = result.data,
                                    totalFound = result.data.size
                                )
                            }
                        }

                        is ResultHttp.Error -> {
                            _state.value = SearchState.Error(result.message)
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
