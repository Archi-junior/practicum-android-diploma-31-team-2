package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.VacanciesInteractorImpl

val domainModule = module {

    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }
}
