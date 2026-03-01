package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.FavoritesVacancyRepositoryImpl
import ru.practicum.android.diploma.data.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.FavoritesVacancyRepository
import ru.practicum.android.diploma.domain.VacanciesRepository

val repositoryModule = module {

    single<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }

    single<FavoritesVacancyRepository> {
        FavoritesVacancyRepositoryImpl(
            get(),
            get()
        )
    }
}
