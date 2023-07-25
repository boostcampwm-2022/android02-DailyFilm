package com.boostcamp.dailyfilm.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR
import com.boostcamp.dailyfilm.presentation.calendar.compose.DateState
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DateViewModel @Inject constructor(
    calendarRepository: CalendarRepository,
    private val syncRepository: SyncRepository,
    private val userId: String,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val calendar = savedStateHandle.get<Calendar>(KEY_CALENDAR)
        ?: throw IllegalStateException("CalendarViewModel - calendar is null")

    val todayCalendar =  createCalendar(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 24)
    }
    private val _dateState = MutableStateFlow(DateState())
    val dateState : StateFlow<DateState> get() = _dateState

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val dayOfWeek = createCalendar(calendar, day = 1).dayOfWeek()
    private val prevCalendar = createCalendar(calendar, month = calendar.month() - 1)
    private val prevMaxDay = prevCalendar.maximum()

    private val _dateFlow = MutableStateFlow(initialDateList())
    val dateFlow: StateFlow<List<DateModel>> = _dateFlow.asStateFlow()

    val itemFlow: Flow<List<DailyFilmItem?>> = calendarRepository.loadFilmInfo(
        getStartAt(getStartCalendar(prevCalendar, prevMaxDay, dayOfWeek)),
        getEndAt(
            calendar.month(),
            getStartCalendar(prevCalendar, prevMaxDay, dayOfWeek)
        )
    )

    private val _reloadFlow = MutableSharedFlow<Pair<Int, DateModel>>()
    val reloadFlow: SharedFlow<Pair<Int, DateModel>> = _reloadFlow.asSharedFlow()

    fun isSynced(year: Int): Boolean = syncRepository.isSynced(year)

    fun syncFilmItem() {
        // ex) 2023-03-17
        val year = calendar.get(Calendar.YEAR)

        // startPrevCalendar
        //  - 0년일 경우 예외 >> 0-01-xx
        //  - 작년 12월 , 2022-12-17
        val startPrevCalendar = createCalendar(Locale.getDefault()).apply {
            if (year == 0) {
                setDate(year, 1)
            } else {
                setDate(year - 1, 12)
            }
        }
        // 31
        val startPrevMaxDay = startPrevCalendar.maximum()

        // 현재 년도의 시작 요일, 2023-01-01, startDayOfWeek = 1
        val startDayOfWeek = createCalendar(year, 1, 1).dayOfWeek()

        // 현재 년도의 11월, 2023-11-xx
        val endPrevCalendar = createCalendar(year, 11, 1)
        // 30
        val endPrevMaxDay = endPrevCalendar.maximum()

        // 12월의 첫 번째 요일, 6
        val endDayOfWeek = createCalendar(year, 12, 1).dayOfWeek()

        viewModelScope.launch {
            syncRepository.addSyncedYear(year)
            syncRepository.startSync(
                userId,
                getStartAt(getStartCalendar(startPrevCalendar, startPrevMaxDay, startDayOfWeek)),
                getEndAt(11, getStartCalendar(endPrevCalendar, endPrevMaxDay, endDayOfWeek))
            )
        }
    }

    fun reloadCalendar(itemList: List<DailyFilmItem?>) {
        val tempCalendar = createCalendar(prevCalendar, day = prevMaxDay - (dayOfWeek - 2))
        val dateModelList = _dateFlow.value.toMutableList()
        val prevDay = tempCalendar.day()
        val prevCnt = if (dayOfWeek == 1) {
            -1
        } else {
            prevMaxDay - prevDay
        }
        val currentMonth = calendar.month()
        val currentMaxDay = calendar.maximum()

        for (i in itemList.indices) {
            val item = itemList[i]
            val prevItem = _dateFlow.value[i]
            if (item == null) return

            val itemDate = dateFormat.parse(item.updateDate) ?: return
            val itemCalendar = createCalendar(itemDate)

            val itemMonth = itemCalendar.month()
            val itemDay = itemCalendar.day()
            val itemYear = itemCalendar.year()

            val index = if (currentMonth > itemMonth) {
                itemDay - prevDay
            } else if (currentMonth == itemMonth) {
                prevCnt + itemDay
            } else {
                prevCnt + currentMaxDay + itemDay
            }

            val currentItem = DateModel(
                itemYear.toString(),
                itemMonth.toString(),
                itemDay.toString(),
                item.text,
                item.videoUrl
            )

            viewModelScope.launch {
                if (prevItem != currentItem) {
                    _reloadFlow.emit(Pair(index, currentItem))
                }
            }

            dateModelList[index] = DateModel(
                itemYear.toString(),
                itemMonth.toString(),
                itemDay.toString(),
                item.text,
                item.videoUrl
            )
        }

        _dateFlow.value = dateModelList
    }

    fun setVideo(index: Int, dateModel: DateModel) {
        val tmpList = _dateFlow.value.toMutableList()
        tmpList[index] = dateModel
        _dateFlow.value = tmpList
    }

    fun initialDateList(): List<DateModel> {
        val tempCalendar = createCalendar(prevCalendar, day = prevMaxDay - (dayOfWeek - 2))
        val dateModelList = mutableListOf<DateModel>()

        for (i in 0 until 42) {
            val year = tempCalendar.year()
            val month = tempCalendar.month()
            val day = tempCalendar.day()

            if (i == 35 && calendar.month() != month) {
                break
            }

            dateModelList.add(
                DateModel(
                    year.toString(),
                    month.toString(),
                    day.toString()
                )
            )
            tempCalendar.addDay(1)
        }

        return dateModelList
    }
}