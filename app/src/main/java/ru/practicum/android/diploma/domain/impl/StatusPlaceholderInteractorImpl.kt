package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.interactor.StatusPlaceholderInteractor
import ru.practicum.android.diploma.domain.repository.VacancyRepository
import ru.practicum.android.diploma.util.UiState

class StatusPlaceholderInteractorImpl(val repository: VacancyRepository) : StatusPlaceholderInteractor {
    override suspend fun searchVacancies(params: Map<String, String>): Flow<List<Vacancy>> {
        return repository.searchVacancies(params)
    }
}
