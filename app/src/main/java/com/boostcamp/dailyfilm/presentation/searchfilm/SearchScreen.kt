package com.boostcamp.dailyfilm.presentation.searchfilm

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme
import com.boostcamp.dailyfilm.presentation.ui.theme.lightGray
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchFilmViewModel,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val lazyPagingItems = viewModel.itemPageFlow.collectAsLazyPagingItems()
    val dateRange: String by viewModel.dateRangeFlow.collectAsStateWithLifecycle()
    var titleVisibility by rememberSaveable { mutableStateOf(true) }
    var searchText by rememberSaveable { mutableStateOf("") }

    DailyFilmTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SearchAppBar(
                    titleVisibility = titleVisibility,
                    searchText = searchText,
                    viewModel = viewModel,
                    focusManager = focusManager,
                    focusRequester = focusRequester,
                    onVisibilityChange = { titleVisibility = it },
                    onSearchTextChange = { searchText = it },
                    onNavigationClick = { viewModel.onNavigationClick() },
                )
            },
        ) {
            Column {
                SearchRangeTextBox(dateRange = dateRange) {
                    viewModel.showDatePicker()
                }
                LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = it) {
                    items(
                        count = lazyPagingItems.itemCount,
                        key = { i -> lazyPagingItems[i]?.videoUrl ?: i },
                    ) { index ->
                        lazyPagingItems[index]?.let {
                            Row(modifier = Modifier.animateItemPlacement()) {
                                FilmCard(it) { viewModel.onClickItem(index) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAppBar(
    titleVisibility: Boolean,
    searchText: String,
    viewModel: SearchFilmViewModel,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
    onVisibilityChange: (Boolean) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onNavigationClick: () -> Unit,
) {
    TopAppBar(
        title = {
            if (titleVisibility) {
                Text("검색")
            } else {
                TextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    singleLine = true,
                    placeholder = { Icon(Icons.Filled.Search, null, tint = Color.Gray) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.searchKeyword(searchText)
                        focusManager.clearFocus()
                    }),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.background,
                        focusedIndicatorColor = MaterialTheme.colors.background,
                        unfocusedIndicatorColor = MaterialTheme.colors.background,
                    ),
                    modifier = Modifier.focusRequester(focusRequester),
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) { Icon(Icons.Filled.ArrowBack, null) }
        },
        actions = {
            IconButton(
                onClick = {
                    if (titleVisibility) {
                        onVisibilityChange(false)
                    } else {
                        onVisibilityChange(true)
                        onSearchTextChange("")
                        viewModel.searchKeyword("")
                    }
                },
            ) {
                if (titleVisibility) {
                    Icon(Icons.Filled.Search, null)
                } else {
                    Icon(Icons.Filled.Close, null)
                }
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
    )

    if (titleVisibility.not()) {
        SideEffect {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun SearchRangeTextBox(dateRange: String, onClickTextBox: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(lightGray, RoundedCornerShape(4.dp))
            .padding(0.dp, 4.dp)
            .clickable { onClickTextBox() },
    ) {
        Icon(Icons.Filled.DateRange, null, modifier = Modifier.padding(4.dp), tint = Color.Black)
        Text(
            text = dateRange,
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black,
            maxLines = 1,
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = TextUnit(20F, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@OptIn(
    ExperimentalGlideComposeApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
private fun FilmCard(
    item: DailyFilmItem,
    onClickItem: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
        backgroundColor = lightGray,
        onClick = { onClickItem() },
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            GlideImage(
                model = item.videoUrl,
                contentDescription = "thumbnail",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3 / 4F),
                contentScale = ContentScale.FillBounds,
            )
            Text(
                text = "${item.updateDate.substring(0, 4)}년 " +
                    "${item.updateDate.substring(4, 6)}월 " +
                    "${item.updateDate.substring(6)}일",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 0.dp),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontSize = TextUnit(16F, TextUnitType.Sp)),
            )
            Text(
                text = item.text,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
}
