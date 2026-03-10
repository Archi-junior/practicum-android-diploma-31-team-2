package ru.practicum.android.diploma.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.mapper.toDomain
import ru.practicum.android.diploma.data.network.ApiRequest
import ru.practicum.android.diploma.data.network.ApiResponse
import ru.practicum.android.diploma.data.network.ApiResponseData
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.AreaRepository
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.getCountries
import ru.practicum.android.diploma.domain.models.getRegionsForCountry
import ru.practicum.android.diploma.domain.models.isRegion

class AreaRepositoryImpl(
    private val networkClient: NetworkClient
) : AreaRepository {

    private var cachedAreas: List<Area>? = null

    override fun getCountries(): Flow<ResultHttp<List<Area>>> = flow {
        val allAreas = getAllAreasFromNetwork()

        emit(
            when (allAreas) {
                is ResultHttp.Success -> {
                    ResultHttp.Success(allAreas.data.getCountries())
                }
                is ResultHttp.Error -> allAreas
                is ResultHttp.NoConnection -> allAreas
            }
        )
    }

    override fun getRegionsByCountry(countryId: Int): Flow<ResultHttp<List<Area>>> = flow {
        val allAreas = getAllAreasFromNetwork()

        emit(
            when (allAreas) {
                is ResultHttp.Success -> {
                    ResultHttp.Success(allAreas.data.getRegionsForCountry(countryId))
                }
                is ResultHttp.Error -> allAreas
                is ResultHttp.NoConnection -> allAreas
            }
        )
    }

    override fun getAllRegions(): Flow<ResultHttp<List<Area>>> = flow {
        val allAreas = getAllAreasFromNetwork()

        emit(
            when (allAreas) {
                is ResultHttp.Success -> {
                    ResultHttp.Success(allAreas.data.filter { it.isRegion() })
                }
                is ResultHttp.Error -> allAreas
                is ResultHttp.NoConnection -> allAreas
            }
        )
    }

    private suspend fun getAllAreasFromNetwork(): ResultHttp<List<Area>> {
        cachedAreas?.let { return ResultHttp.Success(it) }

        return when (val response = networkClient.doRequest(ApiRequest.Areas)) {
            is ApiResponse.Success -> {
                val data = response.data as ApiResponseData.Areas
                val areas = data.areas.map { it.toDomain() }
                cachedAreas = areas
                ResultHttp.Success(areas)
            }
            is ApiResponse.Error -> ResultHttp.Error(response.code, response.message)
            is ApiResponse.NoConnection -> ResultHttp.NoConnection
        }
    }

    override fun getAllAreas(): Flow<ResultHttp<List<Area>>> = flow {
        val allAreas = getAllAreasFromNetwork()
        emit(allAreas)
    }

    override fun searchRegions(query: String, countryId: Int?): Flow<ResultHttp<List<Area>>> = flow {
        val allAreas = getAllAreasFromNetwork()

        when (allAreas) {
            is ResultHttp.Success -> {
                val regions = allAreas.data
                    .filter { it.isRegion() }
                    .filter { countryId == null || it.parentId == countryId }
                    .filter { it.name.contains(query, ignoreCase = true) }
                emit(ResultHttp.Success(regions))
            }
            is ResultHttp.Error -> emit(allAreas)
            is ResultHttp.NoConnection -> emit(allAreas)
        }
    }
}
