package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.VacanciesResult
import ru.practicum.android.diploma.domain.models.Vacancy


interface VacanciesRepository {

    fun search(filter: VacanciesFilter): Flow<ResultHttp<VacanciesResult>>

    fun getDetails(id: String): Flow<ResultHttp<Vacancy>>
}
