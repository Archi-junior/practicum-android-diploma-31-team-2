package ru.practicum.android.diploma.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteVacanciesDao {

    @Insert(entity = FavoriteVacancyEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteVacancyEntity: FavoriteVacancyEntity)

    @Query("DELETE FROM favorite_vacancies WHERE id = :vacancyId")
    suspend fun delete(vacancyId: String)

    @Query("SELECT * FROM favorite_vacancies ORDER BY name")
    suspend fun getAll(): List<FavoriteVacancyEntity>

    @Query("SELECT * FROM favorite_vacancies WHERE id = :id")
    suspend fun getById(id: String): FavoriteVacancyEntity?

}
