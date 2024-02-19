package com.boostcamp.dailyfilm.presentation.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.calendar.model.DateState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarRoute(
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
) {
    val calendarTitle: String by calendarViewModel.calendarFlow.collectAsStateWithLifecycle()
    val pagerState: PagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2) { Int.MAX_VALUE }
    val lottieFAB by rememberLottieComposition(LottieCompositionSpec.Asset("calendar_floating_button.json"))

    val cameraPainter = painterResource(id = R.drawable.baseline_photo_camera_24)
    val galleryPainter = painterResource(id = R.drawable.baseline_picture_in_picture_24)

    var lottieVisibility by rememberSaveable { mutableStateOf(false) }
    var dateState by rememberSaveable { mutableStateOf(DateState.TODAY) }
    var menuState by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            calendarViewModel.getViewPagerPosition(page)
            dateState = when {
                page > pagerState.initialPage -> {
                    DateState.AFTER
                }

                page < pagerState.initialPage -> {
                    DateState.BEFORE
                }

                else -> {
                    DateState.TODAY
                }
            }
        }
    }

    CalendarScreen(
        calendarTitle = { calendarTitle },
        pagerState = { pagerState },
        lottieFAB = { lottieFAB },
        lottieVisibility = { lottieVisibility },
        cameraPainter = { cameraPainter },
        galleryPainter = { galleryPainter },
        dateState = { dateState },
        menuState = { menuState },
        onExpendMenu = { menuState = true },
        onDismissMenu = { menuState = false },
        onMoveToday = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.initialPage)
            }
        },
        onClickUpload = { lottieVisibility = !lottieVisibility },
        calendarViewModel = calendarViewModel,
        coroutineScope = coroutineScope,
        modifier = modifier
    )
}
