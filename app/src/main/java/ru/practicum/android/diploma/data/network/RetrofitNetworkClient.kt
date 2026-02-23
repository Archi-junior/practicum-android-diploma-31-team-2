package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class RetrofitNetworkClient(
    private val context: Context,
    private val apiService: ApiService,
) : NetworkClient {

    override suspend fun doRequest(apiRequest: ApiRequest): ApiResponse<ApiResponseData> {
        if (!isConnected()) {
            return ApiResponse.NoConnection
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = when (apiRequest) {
                    is ApiRequest.Areas -> {
                        ApiResponseData.Areas(apiService.getAreas())
                    }
                    is ApiRequest.Industries -> {
                        ApiResponseData.Industries(apiService.getIndustries())
                    }
                    is ApiRequest.Vacancies -> {
                        val vacanciesResponse = apiService.getVacancies(
                            areaId = apiRequest.areaId,
                            industryId = apiRequest.industryId,
                            text = apiRequest.text,
                            salaryVal = apiRequest.salaryVal,
                            page = apiRequest.page,
                            onlyWithSalary = apiRequest.onlyWithSalary
                        )
                        ApiResponseData.Vacancies(
                            found = vacanciesResponse.found,
                            pages = vacanciesResponse.pages,
                            page = vacanciesResponse.page,
                            items = vacanciesResponse.items
                        )
                    }
                    is ApiRequest.VacancyDetails -> {
                        val vacancy = apiService.getVacancyDetails(apiRequest.id)
                        ApiResponseData.VacancyDetails(vacancy)
                    }
                }
                ApiResponse.Success(response)

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.e("RetrofitNetworkClient", "Network error: ${e.message}", e)
                ApiResponse.NoConnection
            } catch (e: retrofit2.HttpException) {
                val code = e.code()
                val message = e.message()
                Log.e("RetrofitNetworkClient", "HTTP error $code: $message", e)
                ApiResponse.Error(code, message)
            } catch (e: Exception) {
                Log.e("RetrofitNetworkClient", "Unexpected error: ${e.message}", e)
                ApiResponse.Error(HTTP_INTERNAL_ERROR, e.message)
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    companion object {
        const val HTTP_INTERNAL_ERROR = 500
    }
}
