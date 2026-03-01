package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.VacanciesRepository
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.Vacancy

class VacanciesInteractorImpl(
    private val repository: VacanciesRepository
) : VacanciesInteractor{

    override fun search(filter: VacanciesFilter): Flow<ResultHttp<List<Vacancy>>> {
        return repository.search(filter)
    }

    override fun getDetails(id: String): Flow<ResultHttp<Vacancy>> {
        return repository.getDetails(id)
    }
}
