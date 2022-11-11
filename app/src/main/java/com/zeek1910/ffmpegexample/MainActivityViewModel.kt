package com.zeek1910.ffmpegexample

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val timeLapsManager = TimeLapsManager()

    private val _images: MutableStateFlow<Set<Uri>> = MutableStateFlow(emptySet())
    val images: StateFlow<Set<Uri>> = _images.asStateFlow()

    fun onImageAdded(images: List<Uri>) = viewModelScope.launch {
        _images.emit(_images.value + images)
    }

    fun onClearButtonClicked() = viewModelScope.launch { _images.emit(emptySet()) }

    fun onCreateTimelapsButtonClicked() = viewModelScope.launch(Dispatchers.IO) {
        val images = _images.value.map {
            getFileFromUri(getApplication<Application>().applicationContext, it)
        }
        val video = createVideoFile(getApplication<Application>().applicationContext)
        val params = TimeLapsManager.Params(imageDuration = 3)
        timeLapsManager.createTimeLaps(images, video, params) { progress ->
            Timber.d("progress: $progress")
        }
    }

    private suspend fun getFileFromUri(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            context.contentResolver.openInputStream(uri)?.copyTo(it)
                ?: throw FileNotFoundException()
        }
        return file
    }

    private suspend fun createVideoFile(context: Context): File {
        val imagesDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                .absolutePath + "/" + context.getString(R.string.app_name)
        )
        if (!imagesDir.exists()) imagesDir.mkdirs()
        return File(imagesDir, "${System.currentTimeMillis()}.mp4")
    }
}