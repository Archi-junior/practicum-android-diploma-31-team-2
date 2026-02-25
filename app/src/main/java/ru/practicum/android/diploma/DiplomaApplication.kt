package ru.practicum.android.diploma

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.data.db.FavoriteVacanciesDao
import ru.practicum.android.diploma.data.mapper.toDomain
import ru.practicum.android.diploma.data.mapper.toEntity
import ru.practicum.android.diploma.data.network.ApiRequest
import ru.practicum.android.diploma.data.network.ApiResponse
import ru.practicum.android.diploma.data.network.ApiResponseData
import ru.practicum.android.diploma.data.network.NetworkClient

import ru.practicum.android.diploma.di.dataModule
import ru.practicum.android.diploma.di.domainModule
import ru.practicum.android.diploma.di.interactorModule
import ru.practicum.android.diploma.di.networkModule
import ru.practicum.android.diploma.di.repositoryModule
import ru.practicum.android.diploma.di.viewModelModule

class DiplomaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DiplomaApplication)
            modules(
                domainModule,
                dataModule,
                networkModule,
                repositoryModule,
                interactorModule,
                viewModelModule
            )
        }

        val retrofitNetworkClient by inject<NetworkClient>()
        val favoriteVacanciesDao by inject<FavoriteVacanciesDao>()
        val gson by inject<Gson>()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

            val apiResponse = retrofitNetworkClient.doRequest(
                ApiRequest.Vacancies(
                    text = "developer",
                    areaId = 3745,
                    //industryId = 50,
                )
            )
            val vacanciesDto = ((apiResponse as ApiResponse.Success).data as ApiResponseData.Vacancies).items

            favoriteVacanciesDao.insertVacancy(vacanciesDto[0].toDomain().toEntity(gson))
            favoriteVacanciesDao.insertVacancy(vacanciesDto[1].toDomain().toEntity(gson))
            favoriteVacanciesDao.insertVacancy(vacanciesDto[2].toDomain().toEntity(gson))

            val favoriteVacancies = favoriteVacanciesDao.getVacancies()
            val vacancies = favoriteVacancies.map { it.toDomain(gson) }
            Log.d("ApiRequest", vacancies.toString())
        }

    }
}
