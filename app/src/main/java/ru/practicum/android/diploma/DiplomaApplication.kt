package ru.practicum.android.diploma

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.single
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
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.models.VacanciesFilter

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

        val vacanciesInteractor by inject<VacanciesInteractor>()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

            val vacancyDtoInt = vacanciesInteractor.getDetails("4d4d5b2e-0cf3-426d-b517-b6bf86b41d3e").single()
            Log.d("ApiRequest", vacancyDtoInt.toString())

            val vacanciesDtoInt = vacanciesInteractor.search(
                VacanciesFilter(
                    text = "developer",
                    areaId = 3745,
                )
            ).single()
            Log.d("ApiRequest", vacanciesDtoInt.toString())
        }


//        val retrofitNetworkClient by inject<NetworkClient>()
//        val favoriteVacanciesDao by inject<FavoriteVacanciesDao>()
//        val gson by inject<Gson>()
//
//        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
//
//            val apiResponse = retrofitNetworkClient.doRequest(
//                ApiRequest.VacanciesFilter(
//                    text = "developer",
//                    areaId = 3745,
//                    //industryId = 50,
//                )
//            )
//            val vacanciesDto = ((apiResponse as ApiResponse.Success).data as ApiResponseData.Vacancies).items
//
//            favoriteVacanciesDao.insert(vacanciesDto[0].toDomain().toEntity(gson))
//            favoriteVacanciesDao.insert(vacanciesDto[1].toDomain().toEntity(gson))
//            favoriteVacanciesDao.insert(vacanciesDto[2].toDomain().toEntity(gson))
//
//            val favoriteVacancies = favoriteVacanciesDao.getAll()
//            val vacancies = favoriteVacancies.map { it.toDomain(gson) }
//            Log.d("ApiRequest", vacancies.toString())
//
//            favoriteVacanciesDao.delete("4d4d5b2e-0cf3-426d-b517-b6bf86b41d3e")
////            val favoriteVacancy = favoriteVacanciesDao.getById("4d4d5b2e-0cf3-426d-b517-b6bf86b41d3e")
////            Log.d("ApiRequest", favoriteVacancy.toString())
//        }
    }
}
