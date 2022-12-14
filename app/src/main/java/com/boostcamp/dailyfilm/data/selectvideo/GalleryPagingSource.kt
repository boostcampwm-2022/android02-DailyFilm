package com.boostcamp.dailyfilm.data.selectvideo

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.boostcamp.dailyfilm.data.model.VideoItem
import kotlinx.coroutines.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GalleryPagingSource @Inject constructor(
    private val contentResolver: ContentResolver
) : PagingSource<Int, VideoItem>() {

    override fun getRefreshKey(state: PagingState<Int, VideoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoItem> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            val data = loadVideos((pageNumber - 1) * PAGING_SIZE)
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (data.isEmpty()) null else {
                pageNumber + 1
            }
            LoadResult.Page(data, prevKey, nextKey)

        } catch (e: IOException) {
            LoadResult.Error(e)

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun loadVideos(start: Int): List<VideoItem> {

        val collection = getCollection()
        val projection = getProjection()
        val selection = getSelection()
        val selectionArgs = getSelectionArgs()
        val query = getCursor(start, collection, projection, selection, selectionArgs)

        return getVideoItemsByQuery(query)
    }

    private suspend fun getVideoItemsByQuery(query: Cursor?): MutableList<VideoItem> {
        val videos = mutableListOf<VideoItem>()

        withContext(Dispatchers.IO) {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    videos += VideoItem(contentUri, name, duration, size)
                }
            }
        }
        return videos
    }

    private fun getCursor(
        start: Int,
        collection: Uri,
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>
    ) = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        val sortOrder =
            "${MediaStore.Video.Media.DISPLAY_NAME} ASC LIMIT $PAGING_SIZE OFFSET $start"

        contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
    } else {
        val selectionBundle = bundleOf(
            ContentResolver.QUERY_ARG_OFFSET to start,
            ContentResolver.QUERY_ARG_LIMIT to PAGING_SIZE,
            ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED),
            ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
            ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
            ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs
        )

        contentResolver.query(
            collection,
            projection,
            selectionBundle,
            null
        )
    }

    private fun getSelectionArgs() = arrayOf(
        TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS).toString()
    )

    private fun getSelection() = "${MediaStore.Video.Media.DURATION} >= ?"

    private fun getProjection() = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE
    )

    private fun getCollection() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
        const val PAGING_SIZE = 9
    }
}