package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage


@Composable
fun VideoLists(
    modifier: Modifier = Modifier,
    viewModel: SelectVideoViewModel
) {
    val selectedVideo = viewModel.selectedVideo.collectAsState()
    val nestedScrollInterop = rememberNestedScrollInteropConnection()
    val pager = remember { viewModel.loadVideo() }
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    if (lazyPagingItems.itemCount > 0) {
        LaunchedEffect(selectedVideo.value == null) {
            viewModel.chooseVideo(lazyPagingItems[0])
        }
    }

    // RecyclerView 역할 (GridLayout)
    LazyVerticalGrid(
        modifier = modifier
            .padding(top = 3.dp, start = 3.dp, end = 3.dp)
            .nestedScroll(nestedScrollInterop),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        columns = GridCells.Adaptive(minSize = 120.dp),
    ) {

        items(lazyPagingItems.itemCount) { idx ->

            val isSelected = selectedVideo.value == lazyPagingItems[idx]
            val alpha by animateFloatAsState(if (isSelected) 0.5f else 1f)

            VideoGrid(
                modifier = Modifier
                    .clickable { viewModel.chooseVideo(lazyPagingItems[idx]) }
                    .alpha(alpha),
                videoItem = lazyPagingItems[idx],
            )

        }

        // Loading시 Indicator 보이기
        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = colorResource(id = R.color.Primary)
                )
            }
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VideoGrid(
    modifier: Modifier = Modifier,
    videoItem: VideoItem?
) {

    if (videoItem != null) {
        GlideImage(
            modifier = modifier
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
            model = videoItem.uri,
            contentDescription = null
        )
    }
}