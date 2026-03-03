package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.Vacancy

interface SearchInteractor {
    fun searchVacancies(filter: VacanciesFilter): Flow<ResultHttp<List<Vacancy>>>
}
