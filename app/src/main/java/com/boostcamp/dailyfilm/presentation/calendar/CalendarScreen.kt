package com.boostcamp.dailyfilm.presentation.calendar

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.presentation.DailyFilmActivity
import com.boostcamp.dailyfilm.presentation.calendar.compose.CalendarView
import com.boostcamp.dailyfilm.presentation.calendar.model.DateState
import com.boostcamp.dailyfilm.presentation.util.compose.noRippleClickable
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    calendarTitle: () -> String,
    pagerState: () -> PagerState,
    lottieFAB: () -> LottieComposition?,
    lottieVisibility: () -> Boolean,
    cameraPainter: () -> Painter,
    galleryPainter: () -> Painter,
    dateState: () -> DateState,
    menuState: () -> Boolean,

    onExpendMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMoveToday: () -> Unit,
    onClickUpload: () -> Unit,

    calendarViewModel: CalendarViewModel,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalendarTopBar(
                calendarTitle = calendarTitle,
                dateState = dateState,
                menuState = menuState,
                onExpendMenu = onExpendMenu,
                onDismissMenu = onDismissMenu,
                onMoveToday = onMoveToday
            )
        },
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState(),
            beyondBoundsPageCount = 1,
            modifier = modifier.padding(paddingValues),
        ) { page ->
            val position = page - Int.MAX_VALUE / 2
            val currentCalendar = Calendar.getInstance(Locale.getDefault()).apply {
                add(Calendar.MONTH, position)
                if (position != 0) {
                    set(Calendar.DAY_OF_MONTH, 1)
                }
            }

            /*
                page position을 ViewModel의 key로 사용해 ViewModel을 만들고, Factory를 이용해 Calendar 객체를 전달
                -> 단순 Int 값으로도 문제 없이 제대로 작동하지만 key를 만드는 문법? 이 있어도 좋을듯
            */

            CalendarView(
                resetFilm = { dateModelList ->
                    calendarViewModel.emitFilm(dateModelList)
                },
                imgClick = { idx, dateModel ->
                    /* TODO: Navigate to PlayFilm */
                },
                viewModel = dateComposeViewModel(position.toString(), currentCalendar),
            )


            // 테스트용 빈 박스
            /*
            Box(
                modifier = modifier.fillMaxSize()
            ) {  }
             */
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = lottieVisibility(),
                    enter = fadeIn(animationSpec = tween(durationMillis = 800)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400)),
                ) {
                    CircleFloatingButton(
                        onClick = { /* TODO: 영상 촬영 화면 이동 */ },
                        icon = cameraPainter,
                    )
                }

                AnimatedVisibility(
                    visible = lottieVisibility(),
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 800)),
                ) {
                    CircleFloatingButton(
                        onClick = { /* TODO: 영상 선택 화면(갤러리) 이동 */ },
                        icon = galleryPainter,
                    )
                }

                LottieAnimation(
                    composition = lottieFAB(),
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .padding(4.dp, 8.dp)
                        .noRippleClickable { onClickUpload() }
                )
            }
        }
    }
}

// Factory를 이용해 savedStateHandle에 Calnedar를 저장하여 ViewModel 생성
@Composable
fun dateComposeViewModel(key: String, calendar: Calendar): DateComposeViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        DailyFilmActivity.ViewModelFactoryProvider::class.java,
    ).provideDateViewModelFactory()

    return viewModel(key = key, factory = DateComposeViewModel.provideFactory(factory, calendar))
}

@Composable
private fun CalendarTopBar(
    calendarTitle: () -> String,
    dateState: () -> DateState,
    menuState: () -> Boolean,
    onExpendMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMoveToday: () -> Unit,
) {
    TopAppBar(
        title = { Text(calendarTitle()) },
        navigationIcon = {
            IconButton(onClick = { /* TODO: Navigate to DatePickerDialog */ }) {
                Icon(painterResource(id = R.drawable.ic_datepicker_month), null)
            }
        },
        actions = {
            CalendarTopBarActions(dateState, menuState, onExpendMenu, onDismissMenu, onMoveToday)
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
    )
}

@Composable
private fun CalendarTopBarActions(
    dateState: () -> DateState,
    menuState: () -> Boolean,
    onExpendMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMoveToday: () -> Unit,
) {
    when (dateState()) {
        DateState.BEFORE -> {
            IconButton(onClick = { onMoveToday() }) {
                Icon(painterResource(id = R.drawable.ic_double_arrow_right), null)
            }
        }

        DateState.AFTER -> {
            IconButton(onClick = { onMoveToday() }) {
                Icon(painterResource(id = R.drawable.ic_double_arrow_left), null)
            }
        }

        DateState.TODAY -> {
            onDismissMenu()
        }
    }

    IconButton(onClick = { /*TODO: Navigate to TotalFilm*/ }) {
        Icon(painterResource(id = R.drawable.ic_play_circle), null)
    }

    if (dateState() == DateState.BEFORE || dateState() == DateState.AFTER) {
        IconButton(onClick = { onExpendMenu() }) {
            Icon(Icons.Default.MoreVert, null)
        }
        DropdownMenu(
            expanded = menuState(),
            onDismissRequest = { onDismissMenu() },
        ) {
            DropdownMenuItem(onClick = { /*TODO: Navigate to SearchFilm*/ }) {
                Text(text = "검색")
            }
            DropdownMenuItem(onClick = { /*TODO: Navigate to Settings*/ }) {
                Text(text = "설정")
            }
        }
    } else {
        IconButton(onClick = { /*TODO: Navigate to SearchFilm*/ }) {
            Icon(painterResource(id = R.drawable.ic_search), null)
        }
        IconButton(onClick = { /*TODO: Navigate to Settings*/ }) {
            Icon(painterResource(id = R.drawable.ic_settings), null)
        }
    }
}

@Composable
private fun CircleFloatingButton(
    onClick: () -> Unit,
    icon: () -> Painter,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
        modifier = modifier
            .padding(0.dp, 12.dp)
            .width(48.dp)
            .height(48.dp),
        backgroundColor = Color.Gray,
        contentColor = Color.White,
    ) {
        Icon(icon(), null)
    }
}
