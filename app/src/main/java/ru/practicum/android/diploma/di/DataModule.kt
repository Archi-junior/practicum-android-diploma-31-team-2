package ru.practicum.android.diploma.di

import androidx.room.Room
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.db.AppDatabase
import ru.practicum.android.diploma.data.db.FavoriteVacanciesDao

val dataModule = module {
    factory { Gson() }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db")
            .build()
    }

    factory<FavoriteVacanciesDao> {
        get<AppDatabase>().favoriteVacanciesDao()
    }
}
