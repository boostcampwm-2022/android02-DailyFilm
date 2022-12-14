package com.boostcamp.dailyfilm.presentation.totalfilm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TotalFilmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val filmArray = savedStateHandle.get<ArrayList<DateModel>>(CalendarActivity.KEY_FILM_ARRAY)

    private val _currentDateItem = MutableStateFlow(filmArray?.get(0))
    val currentDateItem: StateFlow<DateModel?> = _currentDateItem.asStateFlow()

    private val _isContentShowed = MutableLiveData(true)
    val isContentShowed: LiveData<Boolean> get() = _isContentShowed

    private val _isMuted = MutableLiveData(false)
    val isMuted: LiveData<Boolean> get() = _isMuted


    fun setCurrentDateItem(dateModel: DateModel) {
        _currentDateItem.value = dateModel
    }

    fun changeShowState(){
        _isContentShowed.value = _isContentShowed.value?.not()
    }

    fun changeMuteState(){
        _isMuted.value = _isMuted.value?.not()
    }

}
