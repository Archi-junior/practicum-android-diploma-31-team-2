package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.FavoritesVacancyInteractor
import ru.practicum.android.diploma.domain.FavoritesVacancyRepository
import ru.practicum.android.diploma.domain.ResultDb
import ru.practicum.android.diploma.domain.models.Vacancy

class FavoritesVacancyInteractorImpl(
    private val repository: FavoritesVacancyRepository,
) : FavoritesVacancyInteractor {

    override fun getAll(): Flow<ResultDb<List<Vacancy>>> {
        return repository.getAll()
    }

    override fun getById(id: String): Flow<ResultDb<Vacancy?>> {
        return repository.getById(id)
    }

    override suspend fun insert(vacancy: Vacancy) {
        repository.insert(vacancy)
    }

    override suspend fun delete(id: String) {
        repository.delete(id)
    }
}
