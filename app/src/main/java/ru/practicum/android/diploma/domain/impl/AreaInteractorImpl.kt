package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.practicum.android.diploma.domain.AreaInteractor
import ru.practicum.android.diploma.domain.AreaRepository
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.findCountryByRegion

class AreaInteractorImpl(
    private val repository: AreaRepository
) : AreaInteractor {

    override fun getCountries(): Flow<ResultHttp<List<Area>>> =
        repository.getCountries()

    override fun getRegionsByCountry(countryId: Int): Flow<ResultHttp<List<Area>>> =
        repository.getRegionsByCountry(countryId)

    override fun getAllRegions(): Flow<ResultHttp<List<Area>>> =
        repository.getAllRegions()

    override suspend fun findCountryByRegion(regionId: Int): Area? {
        val result = getAllRegions().first()
        return if (result is ResultHttp.Success) {
            result.data.findCountryByRegion(regionId)
        } else {
            null
        }
    }
}
