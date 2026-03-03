package ru.practicum.android.diploma.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.util.UiState

interface VacancyRepository {
    suspend fun searchVacancies(params: Map<String, String>): Flow<UiState<List<Vacancy>>>
}
