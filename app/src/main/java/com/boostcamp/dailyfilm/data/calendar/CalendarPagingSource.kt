package com.boostcamp.dailyfilm.data.calendar

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.boostcamp.dailyfilm.data.model.DailyFilmItem

class CalendarPagingSource(
    private val startAt: Int,
    private val endAt: Int,
    private val calendarDataSource: CalendarDataSource,
) : PagingSource<Int, DailyFilmItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DailyFilmItem> {
        runCatching {
            val page = params.key ?: 0
            val data = calendarDataSource.loadPagedFilm(startAt, endAt, page).filterNotNull().map { it.mapToDailyFilmItem() }
            return LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else shouldPositive(page - PAGING_SIZE),
                nextKey = if (data.isEmpty()) null else page + data.size,
            )
        }.onFailure {
            return LoadResult.Error(it)
        }
        return LoadResult.Error(Exception("Unknown Error in CalendarPagingSource"))
    }

    override fun getRefreshKey(state: PagingState<Int, DailyFilmItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(PAGING_SIZE)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(PAGING_SIZE)
        }
    }

    private fun shouldPositive(num: Int) = if (num < 0) 0 else num

    companion object {
        const val PAGING_SIZE = 10
    }
}
