package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Area

interface AreaRepository {

    fun getCountries(): Flow<ResultHttp<List<Area>>>

    fun getRegionsByCountry(countryId: Int): Flow<ResultHttp<List<Area>>>

    fun getAllRegions(): Flow<ResultHttp<List<Area>>>
}
