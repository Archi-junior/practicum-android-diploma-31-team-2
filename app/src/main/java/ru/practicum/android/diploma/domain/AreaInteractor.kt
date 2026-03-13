package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Area

interface AreaInteractor {

    fun getCountries(): Flow<ResultHttp<List<Area>>>

    fun getRegionsByCountry(countryId: Int): Flow<ResultHttp<List<Area>>>

    fun getAllRegions(): Flow<ResultHttp<List<Area>>>

    fun getAllAreas(): Flow<ResultHttp<List<Area>>>

    suspend fun findCountryByRegion(regionId: Int): Area?

    fun searchRegions(query: String, countryId: Int? = null): Flow<ResultHttp<List<Area>>>
}
