package ru.practicum.android.diploma.data

import android.database.sqlite.SQLiteException
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.db.FavoriteVacanciesDao
import ru.practicum.android.diploma.data.mapper.toDomain
import ru.practicum.android.diploma.data.mapper.toEntity
import ru.practicum.android.diploma.domain.FavoritesVacancyRepository
import ru.practicum.android.diploma.domain.ResultDb
import ru.practicum.android.diploma.domain.models.Vacancy
import java.io.IOException

class FavoritesVacancyRepositoryImpl(
    private val favoriteVacanciesDao: FavoriteVacanciesDao,
    private val gson: Gson,
) : FavoritesVacancyRepository {

    override fun getAll(): Flow<ResultDb<List<Vacancy>>> = flow {

        emit(
            try {
                val vacanciesEntity = favoriteVacanciesDao.getAll()
                ResultDb.Success(vacanciesEntity.map { it.toDomain(gson) })
            } catch (e: IOException) {
                ResultDb.Error(e.message)
            } catch (e: SQLiteException) {
                ResultDb.Error(e.message)
            }
        )
    }

    override fun getById(id: String): Flow<ResultDb<Vacancy?>> = flow {

        emit(
            try {
                val vacancyEntity = favoriteVacanciesDao.getById(id)
                ResultDb.Success(vacancyEntity?.toDomain(gson))
            } catch (e: IOException) {
                ResultDb.Error(e.message)
            } catch (e: SQLiteException) {
                ResultDb.Error(e.message)
            }
        )
    }

    override suspend fun insert(vacancy: Vacancy) {
        favoriteVacanciesDao.insert(vacancy.toEntity(gson))
    }

    override suspend fun delete(id: String) {
        favoriteVacanciesDao.delete(id)
    }
}
