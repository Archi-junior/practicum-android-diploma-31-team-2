package ru.practicum.android.diploma.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.mapper.toDomain
import ru.practicum.android.diploma.data.network.ApiRequest
import ru.practicum.android.diploma.data.network.ApiResponse
import ru.practicum.android.diploma.data.network.ApiResponseData
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.Result
import ru.practicum.android.diploma.domain.VacanciesRepository
import ru.practicum.android.diploma.domain.models.VacanciesFilter
import ru.practicum.android.diploma.domain.models.Vacancy

class VacanciesRepositoryImpl(
    private val networkClient: NetworkClient
) : VacanciesRepository {

    override fun search(filter: VacanciesFilter): Flow<Result<List<Vacancy>>> = flow {

        val apiRequestFilter = ApiRequest.VacanciesFilter(
            areaId = filter.areaId,
            industryId = filter.industryId,
            text = filter.text,
            salaryVal = filter.salaryVal,
            page = filter.page,
            onlyWithSalary = filter.onlyWithSalary,
        )
        val apiResponse = networkClient.doRequest(apiRequestFilter)
        emit(
            when (apiResponse) {
                is ApiResponse.NoConnection -> Result.NoConnection
                is ApiResponse.Error -> Result.Error(apiResponse.code, apiResponse.message)
                is ApiResponse.Success -> Result.Success(
                    (apiResponse.data as ApiResponseData.Vacancies).items.map { it.toDomain() }
                )
            }
        )
    }

    override fun getDetails(id: String): Flow<Result<Vacancy>> = flow {

        val apiResponse = networkClient.doRequest(ApiRequest.VacancyDetails(id))
        emit(
            when (apiResponse) {
                is ApiResponse.NoConnection -> Result.NoConnection
                is ApiResponse.Error -> Result.Error(apiResponse.code, apiResponse.message)
                is ApiResponse.Success -> Result.Success(
                    (apiResponse.data as ApiResponseData.VacancyDetails).vacancy.toDomain()
                )
            }
        )
    }
}
