package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage


@Composable
fun VideoLists(
    modifier: Modifier = Modifier,
    viewModel: SelectVideoViewModel,
    pager: Pager<Int, VideoItem>
) {
    val selectedVideo = viewModel.selectedVideo.collectAsState()
    val nestedScrollInterop = rememberNestedScrollInteropConnection()
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