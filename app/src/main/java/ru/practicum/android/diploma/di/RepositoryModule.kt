package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.AreaRepositoryImpl
import ru.practicum.android.diploma.data.FavoritesVacancyRepositoryImpl
import ru.practicum.android.diploma.data.VacanciesRepositoryImpl
import ru.practicum.android.diploma.data.impl.PrefsStorageRepositoryImpl
import ru.practicum.android.diploma.data.impl.ShareDataRepositoryImpl
import ru.practicum.android.diploma.domain.AreaRepository
import ru.practicum.android.diploma.domain.FavoritesVacancyRepository
import ru.practicum.android.diploma.domain.VacanciesRepository
import ru.practicum.android.diploma.domain.repository.PrefsStorageRepository
import ru.practicum.android.diploma.domain.repository.ShareDataRepository

val repositoryModule = module {

    single<ShareDataRepository> {
        ShareDataRepositoryImpl(androidContext())
    }

    single<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }

    single<FavoritesVacancyRepository> {
        FavoritesVacancyRepositoryImpl(
            get(),
            get()
        )
    }

    single<PrefsStorageRepository> {
        PrefsStorageRepositoryImpl(
            get(),
            get()
        )
    }

    single<AreaRepository> {
        AreaRepositoryImpl(
            networkClient = get()
        )
    }
}
