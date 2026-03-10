package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Industry

interface IndustriesInteractor {
    fun getIndustries(): Flow<ResultHttp<List<Industry>>>
}
