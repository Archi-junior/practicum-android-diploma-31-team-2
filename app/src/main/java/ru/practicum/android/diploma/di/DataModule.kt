package ru.practicum.android.diploma.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.db.AppDatabase
import ru.practicum.android.diploma.data.db.FavoriteVacanciesDao

val dataModule = module {

    single<Gson> {
        GsonBuilder()
            .serializeNulls()
            .create()
    }

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

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "diploma_preferences",
            Context.MODE_PRIVATE
        )
    }
}
