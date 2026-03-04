package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorite.FavoriteViewModel
import ru.practicum.android.diploma.ui.search.SearchViewModel
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { (vacancyId: String) ->
        VacancyViewModel(
            vacancyId,
            get(),
            get(),
            get()
        )
    }

    viewModel {
        FavoriteViewModel(get())
    }
}
