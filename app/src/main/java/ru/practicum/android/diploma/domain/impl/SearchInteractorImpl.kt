package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.SearchInteractor
import ru.practicum.android.diploma.domain.VacanciesRepository
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.Vacancy

class SearchInteractorImpl(
    private val repository: VacanciesRepository
) : SearchInteractor {

    override fun searchVacancies(filter: VacanciesFilter): Flow<ResultHttp<List<Vacancy>>> {
        return repository.search(filter)
    }
}
