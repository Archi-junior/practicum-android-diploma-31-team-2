package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.impl.StatusPlaceholderInteractorImpl
import ru.practicum.android.diploma.domain.interactor.StatusPlaceholderInteractor

val interactorModule = module {
    single<StatusPlaceholderInteractor> { StatusPlaceholderInteractorImpl(get()) }
}
