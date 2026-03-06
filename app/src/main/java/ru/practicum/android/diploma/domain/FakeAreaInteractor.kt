package ru.practicum.android.diploma.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Area

interface FakeAreaInteractor {

    fun getAll(): Flow<ResultHttp<List<Area>>>
}
