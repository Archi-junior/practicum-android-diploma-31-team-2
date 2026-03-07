package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy

interface FavoritesVacancyRepository {

    fun getAll(): Flow<ResultDb<List<Vacancy>>>

    fun getById(id: String): Flow<ResultDb<Vacancy?>>

    suspend fun insert(vacancy: Vacancy)

    suspend fun delete(id: String)
}
