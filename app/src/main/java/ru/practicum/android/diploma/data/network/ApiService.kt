package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
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
        @Query("area") areaId: Int? = null,
        @Query("industry") industryId: Int? = null,
        @Query("text") text: String? = null,
        @Query("salary") salaryVal: Int? = null,
        @Query("page") page: Int = 1,
        @Query("only_with_salary") onlyWithSalary: Boolean? = null,
    ): VacanciesResponse

    @GET("vacancies/{id}")
    suspend fun getVacancyDetails(
        @Path("id") id: String
    ): VacancyDto
}
