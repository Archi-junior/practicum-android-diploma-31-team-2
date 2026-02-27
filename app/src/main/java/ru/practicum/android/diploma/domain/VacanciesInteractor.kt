package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.Vacancy

interface VacanciesInteractor {

    fun search(filter: VacanciesFilter): Flow<Result<List<Vacancy>>>

    fun getDetails(id: String): Flow<Result<Vacancy>>
}
