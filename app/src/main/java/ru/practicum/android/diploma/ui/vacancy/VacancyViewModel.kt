package ru.practicum.android.diploma.ui.vacancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.FavoritesVacancyInteractor
import ru.practicum.android.diploma.domain.ResultDb
import ru.practicum.android.diploma.domain.ResultHttp
import ru.practicum.android.diploma.domain.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.ShareDataInteractor

class VacancyViewModel(
    private val vacancyId: String,
    private val vacanciesInteractor: VacanciesInteractor,
    private val favoritesVacancyInteractor: FavoritesVacancyInteractor,
    private val shareDataInteractor: ShareDataInteractor
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
                            vacancy = resultHttp.data,
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
                    when (resultDb) {
                        is ResultDb.Error -> {
                            stateLiveData.postValue(VacancyState.Error(resultDb.message.toString()))
                        }
                        is ResultDb.Success -> {
                            if (resultDb.data == null) stateLiveData.postValue(VacancyState.NotFound)
                            else {
                                stateLiveData.postValue(
                                    VacancyState.Content(
                                        vacancy = resultDb.data,
                                        isFavorite = true,
                                        onlyFavoriteChanged = false,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun shareVacancy(url: String) {
        viewModelScope.launch {
            shareDataInteractor.shareUrl(url, R.string.share_url_title)
        }
    }

    fun openEmail(email: String) {
        viewModelScope.launch {
            shareDataInteractor.openEmail(email)
        }
    }

    fun callPhone(phone: String) {
        viewModelScope.launch {
            shareDataInteractor.call(phone)
        }
    }

    fun onAddedToFavorites() {
        viewModelScope.launch {
            val state = stateLiveData.value as VacancyState.Content
            if (state.isFavorite) favoritesVacancyInteractor.delete(vacancyId)
            else favoritesVacancyInteractor.insert(state.vacancy)
            stateLiveData.postValue(
                state.copy(
                    isFavorite = !state.isFavorite,
                    onlyFavoriteChanged = true,
                )
            )
        }
    }

    companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
