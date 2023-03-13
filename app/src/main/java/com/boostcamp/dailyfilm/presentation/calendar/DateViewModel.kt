package com.boostcamp.dailyfilm.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.google.firebase.auth.FirebaseAuth
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val calendar = savedStateHandle.get<Calendar>(KEY_CALENDAR)
        ?: throw IllegalStateException("CalendarViewModel - calendar is null")

    private val dayOfWeek = Calendar.getInstance().apply {
        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK)

    private val prevCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
    }

    private val prevMaxDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: error("Unknown User")

    private val _dateFlow = MutableStateFlow(initialDateList())
    val dateFlow: StateFlow<List<DateModel>> = _dateFlow.asStateFlow()

    val itemFlow: Flow<List<DailyFilmItem?>> = calendarRepository.loadFilmInfo(
        getStartAt(getStartCalendar(prevCalendar, prevMaxDay, dayOfWeek)),
        getEndAt(
            calendar.get(Calendar.MONTH),
            getStartCalendar(prevCalendar, prevMaxDay, dayOfWeek)
        )
    )

    private val _reloadFlow = MutableSharedFlow<Pair<Int, DateModel>>()
    val reloadFlow: SharedFlow<Pair<Int, DateModel>> = _reloadFlow.asSharedFlow()

    private fun getStartAt(startCalendar: Calendar): String {
        return dateFormat.format(startCalendar.time)
    }

    private fun getEndAt(currentMonth: Int, startCalendar: Calendar): String {
        val startMonth = startCalendar.get(Calendar.MONTH)
        startCalendar.add(Calendar.DAY_OF_MONTH, 34)
        if (currentMonth != startMonth) {
            val maxDay = startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val currentDay = startCalendar.get(Calendar.DAY_OF_MONTH)
            if (currentMonth == startCalendar.get(Calendar.MONTH) && maxDay != currentDay) {
                startCalendar.add(Calendar.DAY_OF_MONTH, 7)
            }
        }
        return dateFormat.format(startCalendar.time)
    }

    private fun getStartCalendar(
        prevCalendar: Calendar,
        prevMaxDay: Int,
        dayOfWeek: Int
    ): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            val day = if (dayOfWeek == 1) {
                add(Calendar.MONTH, 1)
                1
            } else {
                prevMaxDay - (dayOfWeek - 2)
            }
            set(Calendar.DAY_OF_MONTH, day)
        }
    }

    fun isSynced(year: Int): Boolean = syncRepository.isSynced(year)

    fun syncFilmItem() {
        val year = calendar.get(Calendar.YEAR)

        val startPrevCalendar = if (year == 0) {
            Calendar.getInstance(Locale.getDefault()).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, 0)
            }
        } else {
            Calendar.getInstance(Locale.getDefault()).apply {
                set(Calendar.YEAR, year - 1)
                set(Calendar.MONTH, 11)
            }
        }
        val startPrevMaxDay = startPrevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val startDayOfWeek = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_YEAR, 1)
        }.get(Calendar.DAY_OF_WEEK)

        val endPrevCalendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, 10)
        }
        val endPrevMaxDay = endPrevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val endDayOfWeek = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, 11)
            set(Calendar.DAY_OF_MONTH, 1)
        }.get(Calendar.DAY_OF_WEEK)

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
        val tempCalendar = Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        }

        val dateModelList = _dateFlow.value.toMutableList()
        val prevDay = tempCalendar.get(Calendar.DAY_OF_MONTH)
        val prevCnt = if (dayOfWeek == 1) {
            -1
        } else {
            prevMaxDay - prevDay
        }

        val currentMonth = calendar.get(Calendar.MONTH)
        val currentMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in itemList.indices) {
            val item = itemList[i]
            val prevItem = _dateFlow.value[i]
            if (item == null) return

            val itemDate = dateFormat.parse(item.updateDate) ?: return
            val itemCalendar = Calendar.getInstance().apply {
                time = itemDate
            }

            val itemMonth = itemCalendar.get(Calendar.MONTH)
            val itemDay = itemCalendar.get(Calendar.DAY_OF_MONTH)
            val itemYear = itemCalendar.get(Calendar.YEAR)

            val index = if (currentMonth > itemMonth) {
                itemDay - prevDay
            } else if (currentMonth == itemMonth) {
                prevCnt + itemDay
            } else {
                prevCnt + currentMaxDay + itemDay
            }

            val currentItem = DateModel(
                itemYear.toString(),
                (itemMonth + 1).toString(),
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
                (itemMonth + 1).toString(),
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
        val tempCalendar = Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        }

        val dateModelList = mutableListOf<DateModel>()

        for (i in 0 until 42) {
            val year = tempCalendar.get(Calendar.YEAR)
            val month = tempCalendar.get(Calendar.MONTH) + 1
            val dayOfMonth = tempCalendar.get(Calendar.DAY_OF_MONTH)

            if (i == 35 && calendar.get(Calendar.MONTH) + 1 != month) {
                break
            }

            dateModelList.add(
                DateModel(
                    year.toString(),
                    month.toString(),
                    dayOfMonth.toString()
                )
            )
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dateModelList
    }
}
