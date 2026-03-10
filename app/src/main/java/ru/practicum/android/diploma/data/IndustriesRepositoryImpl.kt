package ru.practicum.android.diploma.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.mapper.toDomain
import ru.practicum.android.diploma.data.network.ApiRequest
import ru.practicum.android.diploma.data.network.ApiResponse
import ru.practicum.android.diploma.data.network.ApiResponseData
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.IndustriesRepository
import ru.practicum.android.diploma.domain.models.Industry

class IndustriesRepositoryImpl(
    private val networkClient: NetworkClient
) : IndustriesRepository {

    override fun getIndustries(): Flow<ResultHttp<List<Industry>>> = flow {
        val response = networkClient.doRequest(ApiRequest.Industries)

        when (response) {
            is ApiResponse.Success -> {
                val data = response.data
                if (data is ApiResponseData.Industries) {
                    emit(ResultHttp.Success(data.industries.map { it.toDomain() }))
                } else {
                    emit(
                        ResultHttp.Error(
                            RetrofitNetworkClient.Companion.HTTP_INTERNAL_ERROR,
                            "Invalid response type"
                        )
                    )
                }
            }

            is ApiResponse.Error -> {
                emit(ResultHttp.Error(response.code, response.message))
            }

            is ApiResponse.NoConnection -> {
                emit(ResultHttp.NoConnection)
            }
        }
    }
}
