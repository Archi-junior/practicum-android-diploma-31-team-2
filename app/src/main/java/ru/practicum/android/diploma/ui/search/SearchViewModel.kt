package ru.practicum.android.diploma.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.VacanciesResult
import ru.practicum.android.diploma.domain.models.Vacancy

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val vacanciesInteractor: VacanciesInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>(SearchState.Start)
    val state: LiveData<SearchState> = _state

    private val _searchQuery = MutableStateFlow("")

    private var currentFilter = VacanciesFilter(page = 1)
    private var isSearching = false
    private var isLoadingNextPage = false
    private var currentQuery = ""
    private var allVacancies = mutableListOf<Vacancy>()
    private var currentPage = 1
    private var totalPages = 1

    init {
        _searchQuery
            .debounce(DEBOUNCE_TIMEOUT)
            .distinctUntilChanged()
            .filter { query ->
                if (query.isBlank()) {
                    resetSearch()
                    false
                } else {
                    true
                }
            }
            .onEach { query ->
                performSearch(query, reset = true)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
        resetSearch()
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (canLoadNextPage(currentState)) {
            performSearch(currentQuery, reset = false)
        }
    }

    private fun canLoadNextPage(currentState: SearchState?): Boolean {
        return currentState is SearchState.Content &&
                !currentState.isLoadingNextPage &&
                currentPage < totalPages &&
                !isLoadingNextPage
    }

    private fun resetSearch() {
        allVacancies.clear()
        currentPage = 1
        totalPages = 1
        isSearching = false
        isLoadingNextPage = false
        _state.value = SearchState.Start
    }

    private fun performSearch(query: String, reset: Boolean) {
        if (isSearching || (isLoadingNextPage && !reset)) return

        val filterSettings = settingsInteractor.getFilterSettings()
        val areaId = filterSettings?.region?.id ?: filterSettings?.country?.id

        viewModelScope.launch {
            if (reset) {
                isSearching = true
                currentPage = 1
                allVacancies.clear()
                _state.value = SearchState.Loading
                currentQuery = query
            } else {
                isLoadingNextPage = true
                currentPage++

                val currentList = allVacancies.toList()
                _state.value = SearchState.Content(
                    vacancies = currentList,
                    totalFound = (_state.value as? SearchState.Content)?.totalFound ?: 0,
                    currentPage = currentPage - 1,
                    totalPages = totalPages,
                    isLoadingNextPage = true
                )
            }

            currentFilter = VacanciesFilter(
                areaId = areaId,
                industryId = filterSettings?.industry?.id,
                text = query,
                salaryVal = filterSettings?.salary,
                page = if (reset) 1 else currentPage,
                onlyWithSalary = filterSettings?.onlyWithSalary
            )

            vacanciesInteractor.search(currentFilter)
                .collectLatest { result ->
                    when (result) {
                        is ResultHttp.Success -> handleSuccess(result.data, reset)
                        is ResultHttp.Error -> handleError(result.message, reset)
                        is ResultHttp.NoConnection -> handleNoConnection(reset)
                    }
                    if (reset) {
                        isSearching = false
                    } else {
                        isLoadingNextPage = false
                    }
                }
        }
    }

    fun applyFilters() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            performSearch(currentQuery, reset = true)
        }
    }

    private fun handleSuccess(result: VacanciesResult, reset: Boolean) {
        totalPages = result.pages

        if (reset) {
            allVacancies.clear()
            allVacancies.addAll(result.vacancies)
            currentPage = 1
        } else {
            val newVacancies = result.vacancies.filter { newVacancy ->
                allVacancies.none { it.id == newVacancy.id }
            }
            allVacancies.addAll(newVacancies)
        }

        if (allVacancies.isEmpty()) {
            _state.value = SearchState.Empty
        } else {
            _state.value = SearchState.Content(
                vacancies = allVacancies.toList(),
                totalFound = result.found,
                currentPage = result.page,
                totalPages = result.pages,
                isLoadingNextPage = false
            )
        }
    }

    private fun handleError(message: String?, reset: Boolean) {
        if (reset) {
            _state.value = SearchState.Error(message)
        } else {
            _state.value = SearchState.PaginationError(message)

            _state.value = SearchState.Content(
                vacancies = allVacancies.toList(),
                totalFound = (_state.value as? SearchState.Content)?.totalFound ?: 0,
                currentPage = currentPage - 1,
                totalPages = totalPages,
                isLoadingNextPage = false
            )
        }
    }

    private fun handleNoConnection(reset: Boolean) {
        if (reset) {
            _state.value = SearchState.NoConnection
        } else {
            _state.value = SearchState.PaginationNoConnection()

            _state.value = SearchState.Content(
                vacancies = allVacancies.toList(),
                totalFound = (_state.value as? SearchState.Content)?.totalFound ?: 0,
                currentPage = currentPage - 1,
                totalPages = totalPages,
                isLoadingNextPage = false
            )
        }
    }

    companion object {
        const val DEBOUNCE_TIMEOUT = 2000L
    }
}
