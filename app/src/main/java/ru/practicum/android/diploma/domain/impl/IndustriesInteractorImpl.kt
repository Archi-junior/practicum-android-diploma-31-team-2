package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.IndustriesInteractor
import ru.practicum.android.diploma.domain.IndustriesRepository
import ru.practicum.android.diploma.domain.models.Industry

class IndustriesInteractorImpl(
    private val repository: IndustriesRepository
) : IndustriesInteractor {

    override fun getIndustries(): Flow<ResultHttp<List<Industry>>> {
        return repository.getIndustries()
    }
}
