package com.boostcamp.dailyfilm.presentation.selectvideo

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class SelectVideoViewModel @Inject constructor(
   private val selectVideoRepository: GalleryVideoRepository
):ViewModel() {



    fun loadVideo(): Flow<PagingData<VideoItem>> {
        return selectVideoRepository.loadVideo().cachedIn(viewModelScope).flowOn(Dispatchers.IO)
    }

}