package ru.practicum.android.diploma.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.util.UiState

interface StatusPlaceholderInteractor {
    suspend fun searchVacancies(params: Map<String, String>): Flow<List<Vacancy>>
}
