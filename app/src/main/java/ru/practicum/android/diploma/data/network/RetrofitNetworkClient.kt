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

    override suspend fun doRequest(apiRequest: ApiRequest): ApiResponse {

        if (!isConnected()) return ApiResponse().apply { noConnection = true }

        return withContext(Dispatchers.IO) {
            try {
                when (apiRequest) {
                    is ApiRequest.Areas -> AreasResponse(apiService.getAreas())
                    is ApiRequest.Industries -> IndustriesResponse(apiService.getIndustries())
                    is ApiRequest.Vacancies -> apiService.getVacancies(
                        apiRequest.areaId,
                        apiRequest.industryId,
                        apiRequest.text,
                        apiRequest.salaryVal,
                        apiRequest.page,
                        apiRequest.onlyWithSalary,
                    )
                    is ApiRequest.VacancyDetails -> VacancyDetailsResponse(apiService.getVacancyDetails(apiRequest.id))
                }.apply { resultCode = HTTP_OK }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.e("ApiService", "Network error", e)
                ApiResponse().apply { noConnection = true }
            } catch (e: Exception) {
                Log.e("ApiService", "doRequest failed", e)
                ApiResponse().apply { resultCode = HTTP_INTERNAL_ERROR }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    companion object {
        const val HTTP_OK = 200
        const val HTTP_INTERNAL_ERROR = 500
    }
}
