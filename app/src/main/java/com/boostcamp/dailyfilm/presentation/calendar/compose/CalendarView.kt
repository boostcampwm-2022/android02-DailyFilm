package com.boostcamp.dailyfilm.presentation.calendar.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.presentation.calendar.DateViewModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.util.compose.noRippleClickable
import com.boostcamp.dailyfilm.presentation.util.compose.rememberLifecycleEvent
import com.boostcamp.dailyfilm.presentation.util.createCalendar
import com.boostcamp.dailyfilm.presentation.util.month
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.util.Calendar

@Composable
fun CalendarView(
    viewModel: DateViewModel,
    resetFilm: (List<DateModel>) -> Unit,
    imgClick: (Int, DateModel) -> Unit,
    onMovePlayFilm: (Int, DateModel) -> Unit,
) {
    val lifecycleEvent = rememberLifecycleEvent()
    val itemList by viewModel.itemFlow.collectAsStateWithLifecycle(initialValue = null)
    val reloadList by viewModel.dateFlow.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
    val dateState by viewModel.dateState.collectAsStateWithLifecycle()

    CalendarView(
        lifecycleEvent = lifecycleEvent,
        itemList = itemList,
        reloadList = reloadList,
        dateState = dateState,
        currentCalendar = viewModel.calendar,
        todayCalendar = viewModel.todayCalendar,
        reloadCalendar = {
            viewModel.reloadCalendar(it)
        },
        resetFilm = resetFilm,
        imgClick = imgClick,
        onMovePlayFilm = onMovePlayFilm
    )
}

@Composable
fun CalendarView(
    lifecycleEvent: Lifecycle.Event,
    itemList: List<DailyFilmItem?>?,
    reloadList: List<DateModel>,
    dateState: DateState,
    currentCalendar: Calendar,
    todayCalendar: Calendar,
    reloadCalendar: (List<DailyFilmItem?>) -> Unit,
    resetFilm: (List<DateModel>) -> Unit,
    imgClick: (Int, DateModel) -> Unit,
    onMovePlayFilm: (Int, DateModel) -> Unit,
) {

    val textSize = 12.sp
    val textHeight = with(LocalDensity.current) {
        textSize.toPx() + 10
    }.toInt()

    LaunchedEffect(lifecycleEvent) {
        when (lifecycleEvent) {
            Lifecycle.Event.ON_PAUSE -> {
                dateState.selectedDay = null
            }
            else -> {}
        }
    }
    LaunchedEffect(key1 = reloadList) {
        resetFilm(
            reloadList.filter { dateModel -> dateModel.videoUrl != null }
        )
    }

    LaunchedEffect(key1 = itemList) {
        reloadCalendar(itemList ?: return@LaunchedEffect)
    }

    CustomCalendarView(
        textHeight = textHeight,
        textSize = textSize,
        reloadList = reloadList,
        currentCalendar = currentCalendar,
        todayCalendar = todayCalendar,
        dateState = dateState,
        imgClick = imgClick,
        onMovePlayFilm = onMovePlayFilm
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun DateImage(background: Color, alpha: Float, url: String?, onClick: () -> Unit) {
    /*val painter = rememberAsyncImagePainter(url)
    val state = painter.state

    val transition by animateFloatAsState(
        targetValue = if (state is AsyncImagePainter.State.Success) 1f else 0f, label = ""
    )
    Image(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .alpha(transition)
            .padding(2.dp)
            .clip(RoundedCornerShape(5.dp))
            .noRippleClickable(onClick = onClick),
        painter = painter,
        contentDescription = "custom transition based on painter state",
    )*/

    GlideImage(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .alpha(alpha)
            .padding(2.dp)
            .clip(RoundedCornerShape(5.dp))
            .noRippleClickable(onClick = onClick),
        contentScale = ContentScale.Crop,
        model = url,
        contentDescription = ""
    )
}

@Composable
private fun CustomCalendarView(
    textHeight: Int,
    textSize: TextUnit,
    reloadList: List<DateModel>,
    currentCalendar: Calendar,
    todayCalendar: Calendar,
    dateState: DateState,
    imgClick: (Int, DateModel) -> Unit,
    onMovePlayFilm: (Int, DateModel) -> Unit,
) {
    CustomCalendarView(
        textHeight = textHeight
    ) {
        reloadList.forEachIndexed { index, dateModel ->
            println("dateModel: $dateModel")
            val isNotCurrentMonth = isNotCurrentMonth(
                dateModel,
                currentCalendar.month(),
                todayCalendar
            )
            dateState.isCurrentMonth = isNotCurrentMonth

            Text(
                modifier = Modifier
                    .alpha(dateState.alpha)
                    .background(dateState.isSelected(index)),
                text = dateModel.day,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                fontSize = textSize
            )
            DateImage(
                background = dateState.isSelected(index),
                alpha = dateState.alpha,
                url = dateModel.videoUrl
            ) {
                println("DateImage: onCLick")
                if (!isNotCurrentMonth) {
                    println("isNotCurrentMonth: $isNotCurrentMonth")
                    dateState.apply {
                        imgClick(index, dateModel)
                        selectedDay = if (dateModel.videoUrl != null) {
                            onMovePlayFilm(index, dateModel)
                            null
                        } else {
                            index
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CustomCalendarView(textHeight: Int, content: @Composable () -> Unit) {

    val lineColor = MaterialTheme.colors.primary

    Layout(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    repeat(5) { idx ->
                        val y = (idx + 1) * size.height / 6
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2f
                        )
                    }
                }
            },
        content = content,
    ) { measureables, constraints ->

        val dayWidth = constraints.maxWidth / 7
        val dayHeight = constraints.maxHeight / 6

        val placeables = measureables.mapIndexed { idx, measurable ->
            measurable.measure(
                when (idx % 2) {
                    0 -> constraints.fixConstraints(width = dayWidth, height = textHeight)
                    1 -> constraints.fixConstraints(
                        width = dayWidth,
                        height = dayHeight - textHeight
                    )

                    else -> constraints
                }
            )
        }

        layout(constraints.maxWidth, constraints.maxHeight) {

            placeables.forEachIndexed { index, placeable ->
                val idx = index / 2

                val verticalIdx = idx / 7
                val horizontalIdx = idx % 7

                val left = horizontalIdx * dayWidth
                val top = verticalIdx * dayHeight

                when (index % 2) {
                    0 -> placeable.placeRelative(x = left, y = top)
                    1 -> placeable.placeRelative(x = left, y = top + textHeight)
                }
            }
        }
    }
}

private fun isNotCurrentMonth(
    dateModel: DateModel,
    currentMonth: Int,
    todayCalendar: Calendar
): Boolean {
    val itemCalendar = with(dateModel) {
        createCalendar(year.toInt(), month.toInt() - 1, day.toInt())
    }
    return dateModel.month.toInt() != currentMonth ||
            itemCalendar.timeInMillis > todayCalendar.timeInMillis
}

private fun Constraints.fixConstraints(width: Int = maxWidth, height: Int = maxHeight) = copy(
    minWidth = width,
    maxWidth = width,
    minHeight = height,
    maxHeight = height
)