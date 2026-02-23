package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RetrofitNetworkClient(
    private val context: Context,
    private val apiService: ApiService,
) : NetworkClient {

    @Suppress("TooGenericExceptionCaught")
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
                        val vacanciesResponse = apiService.getVacancies(apiRequest.toQueryMap())
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
            } catch (e: JsonParseException) {
                Log.e("RetrofitNetworkClient", "JSON parsing error: ${e.message}", e)
                ApiResponse.Error(HTTP_INTERNAL_ERROR, "Data parsing error")
            } catch (e: SocketTimeoutException) {
                Log.e("RetrofitNetworkClient", "Timeout error: ${e.message}", e)
                ApiResponse.NoConnection
            } catch (e: UnknownHostException) {
                Log.e("RetrofitNetworkClient", "Unknown host: ${e.message}", e)
                ApiResponse.NoConnection
            } catch (e: RuntimeException) {
                Log.e("RetrofitNetworkClient", "Unexpected runtime error: ${e.message}", e)
                ApiResponse.Error(HTTP_INTERNAL_ERROR, e.message)
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
        const val HTTP_INTERNAL_ERROR = 500
    }
}
