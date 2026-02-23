package ru.practicum.android.diploma.di

import androidx.room.Room
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    factory { Gson() }

    // Экземпляр датабазы. Раскоментировать как только Вовчик выполнит второй таск
    single { //Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }
}
