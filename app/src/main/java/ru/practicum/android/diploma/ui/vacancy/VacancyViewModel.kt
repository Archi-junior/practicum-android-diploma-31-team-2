package ru.practicum.android.diploma.ui.vacancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.FavoritesVacancyInteractor
import ru.practicum.android.diploma.domain.ResultDb
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.VacanciesInteractor

class VacancyViewModel(
    private val vacancyId: String,
    private val vacanciesInteractor: VacanciesInteractor,
    private val favoritesVacancyInteractor: FavoritesVacancyInteractor
) : ViewModel() {

    private var stateLiveData = MutableLiveData<VacancyState>(VacancyState.Loading)
    fun observeState(): LiveData<VacancyState> = stateLiveData

    init {
        viewModelScope.launch {
            val resultDb = favoritesVacancyInteractor.getById(vacancyId).single()
            when (val resultHttp = vacanciesInteractor.getDetails(vacancyId).single()) {
                is ResultHttp.Success -> {
                    stateLiveData.postValue(
                        VacancyState.Content(
                            vacancy = resultHttp.data.copy(),
                            isFavorite = resultDb is ResultDb.Success && resultDb.data != null,
                            onlyFavoriteChanged = false,
                        )
                    )
                }
                is ResultHttp.Error -> {
                    if (resultHttp.code == HTTP_NOT_FOUND) stateLiveData.postValue(VacancyState.NotFound)
                    else stateLiveData.postValue(VacancyState.Error(resultHttp.message.toString()))
                }
                is ResultHttp.NoConnection -> {
                    stateLiveData.postValue(VacancyState.Error(""))
                }
            }
        }
    }

    companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
