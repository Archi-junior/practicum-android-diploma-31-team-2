package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.data.dto.AreaDto
import ru.practicum.android.diploma.data.dto.IndustryDto
import ru.practicum.android.diploma.data.dto.VacancyDto

interface ApiService {

    @GET("areas")
    suspend fun getAreas(): List<AreaDto>

    @GET("industries")
    suspend fun getIndustries(): List<IndustryDto>

    @GET("vacancies")
    suspend fun getVacancies(
        @QueryMap params: Map<String, String>
    ): VacanciesResponse

    @GET("vacancies/{id}")
    suspend fun getVacancyDetails(
        @Path("id") id: String
    ): VacancyDto
}
