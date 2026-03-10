package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorite.FavoriteViewModel
import ru.practicum.android.diploma.ui.filters.SharedViewModel
import ru.practicum.android.diploma.ui.region.RegionChooseViewModel
import ru.practicum.android.diploma.ui.search.SearchViewModel
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            get(),
            get()
        )
    }

    viewModel {
        SharedViewModel(
            areaInteractor = get(),
            industryInteractor = get(),
            settingsInteractor = get()
        )
    }

    viewModel { RegionChooseViewModel(areaInteractor = get()) }

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
