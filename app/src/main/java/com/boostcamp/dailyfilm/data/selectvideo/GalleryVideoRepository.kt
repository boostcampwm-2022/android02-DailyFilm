package com.boostcamp.dailyfilm.data.selectvideo

import android.content.ContentResolver
import androidx.paging.*
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.data.selectvideo.local.GalleryPagingSource
import com.boostcamp.dailyfilm.data.selectvideo.remote.GalleryDataRemoteSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface GalleryVideoRepository {
    fun loadVideo(): Flow<PagingData<VideoItem>>
    fun uploadVideo(videoItem: VideoItem) : Flow<Boolean>

}

class GalleryVideoRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val galleryDataRemoteSource: GalleryDataRemoteSource
):GalleryVideoRepository{
    override fun loadVideo(): Flow<PagingData<VideoItem>> {
        return Pager(config = PagingConfig(pageSize = GalleryPagingSource.PAGING_SIZE)) {
            GalleryPagingSource(contentResolver)
        }.flow
    }

    override fun uploadVideo(videoItem: VideoItem) = galleryDataRemoteSource.uploadVideo(videoItem)

}