package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.FavoritesVacancyInteractor
import ru.practicum.android.diploma.domain.IndustriesInteractor
import ru.practicum.android.diploma.domain.SettingsInteractor
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.FavoritesVacancyInteractorImpl
import ru.practicum.android.diploma.domain.impl.IndustriesInteractorImpl
import ru.practicum.android.diploma.domain.impl.SettingsInteractorImpl
import ru.practicum.android.diploma.domain.impl.ShareDataInteractor
import ru.practicum.android.diploma.domain.impl.ShareDataInteractorImpl
import ru.practicum.android.diploma.domain.impl.VacanciesInteractorImpl

val interactorModule = module {

    factory<ShareDataInteractor> {
        ShareDataInteractorImpl(get())
    }

    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }
    factory<IndustriesInteractor> {
        IndustriesInteractorImpl(get())
    }

    factory<FavoritesVacancyInteractor> {
        FavoritesVacancyInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}
