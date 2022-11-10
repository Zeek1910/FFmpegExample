package com.zeek1910.ffmpegexample

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _images: MutableStateFlow<Set<Uri>> = MutableStateFlow(emptySet())
    val images: StateFlow<Set<Uri>> = _images.asStateFlow()

    fun onImageAdded(images: List<Uri>) = viewModelScope.launch {
        _images.emit(_images.value + images)
    }

    fun onClearButtonClicked() = viewModelScope.launch { _images.emit(emptySet()) }

    fun onCreateTimelapsButtonClicked() = viewModelScope.launch(Dispatchers.IO) {
        val command = generateCommand(_images.value)
        Timber.d(command)
        val result = FFmpeg.execute(command)
        Timber.d("result = $result")
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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .absolutePath + "/" + context.getString(R.string.app_name)
        )
        if (!imagesDir.exists()) imagesDir.mkdirs()
        return File(imagesDir, "${System.currentTimeMillis()}.mp4")
    }

    private suspend fun generateCommand(images: Set<Uri>): String {
        val stringBuilder = StringBuilder()
        for (uri in images) {
            val file = getFileFromUri(getApplication<Application>().baseContext, uri)
            stringBuilder.append(" -framerate 25 -t 124 -loop 1 -i ${file.absolutePath}")
        }
        stringBuilder.append(" -b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast ${createVideoFile(getApplication<Application>().baseContext).absolutePath}")
        return stringBuilder.toString()
    }

}