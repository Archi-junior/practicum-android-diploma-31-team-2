package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.FavoritesVacancyInteractor
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.FavoritesVacancyInteractorImpl
import ru.practicum.android.diploma.domain.impl.VacanciesInteractorImpl

val interactorModule = module {

    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }

    factory<FavoritesVacancyInteractor> {
        FavoritesVacancyInteractorImpl(get())
    }
}
