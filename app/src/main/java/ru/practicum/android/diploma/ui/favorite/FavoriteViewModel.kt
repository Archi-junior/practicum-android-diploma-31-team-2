package ru.practicum.android.diploma.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.FavoritesVacancyInteractor
import ru.practicum.android.diploma.domain.ResultDb

class FavoriteViewModel(
    private val favoritesVacancyInteractor: FavoritesVacancyInteractor
) : ViewModel() {

    private var stateLiveData = MutableLiveData<FavoriteState>(FavoriteState.Loading)
    fun observeState(): LiveData<FavoriteState> = stateLiveData

    fun updateFavoriteVacancies() {
        viewModelScope.launch {
            stateLiveData.postValue(FavoriteState.Loading)
            when (val resultDb = favoritesVacancyInteractor.getAll().single()) {
                is ResultDb.Success -> {
                    stateLiveData.postValue(FavoriteState.Content(resultDb.data))
                }
                is ResultDb.Error -> {
                    stateLiveData.postValue(FavoriteState.Error(resultDb.message.toString()))
                }
            }
        }
    }
}
