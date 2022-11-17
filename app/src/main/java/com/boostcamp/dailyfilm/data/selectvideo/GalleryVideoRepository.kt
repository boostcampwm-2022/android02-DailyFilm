package com.boostcamp.dailyfilm.data.selectvideo

import android.content.ContentResolver
import androidx.paging.*
import com.boostcamp.dailyfilm.data.model.VideoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface GalleryVideoRepository {
    fun loadVideo(): Flow<PagingData<VideoItem>>
}

class GalleryVideoRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
):GalleryVideoRepository{
    override fun loadVideo(): Flow<PagingData<VideoItem>> {
        return Pager(config = PagingConfig(pageSize = GalleryPagingSource.PAGING_SIZE)) {
            GalleryPagingSource(contentResolver)
        }.flow
    }
}