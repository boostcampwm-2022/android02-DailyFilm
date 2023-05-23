package com.boostcamp.dailyfilm.data.selectvideo

import android.content.ContentResolver
import androidx.paging.*
import com.boostcamp.dailyfilm.data.model.VideoItem
import javax.inject.Inject

interface GalleryVideoRepository {
    fun loadVideo(): Pager<Int, VideoItem>

}

class GalleryVideoRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : GalleryVideoRepository {

    override fun loadVideo(): Pager<Int, VideoItem> = Pager(config = PagingConfig(pageSize = GalleryPagingSource.PAGING_SIZE)) {
        GalleryPagingSource(contentResolver)
    }

}