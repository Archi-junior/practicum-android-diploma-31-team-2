package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Industry

interface FakeIndustryInteractor {

    fun getAll(): Flow<ResultHttp<List<Industry>>>
}
