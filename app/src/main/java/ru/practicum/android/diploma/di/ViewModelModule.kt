package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {

    viewModel { (vacancyId: String) ->
        VacancyViewModel(
            vacancyId,
            get(),
            get(),
        )
    }
}
