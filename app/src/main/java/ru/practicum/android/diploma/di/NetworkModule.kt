package ru.practicum.android.diploma.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { createRetrofit() }

}

private fun createRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
