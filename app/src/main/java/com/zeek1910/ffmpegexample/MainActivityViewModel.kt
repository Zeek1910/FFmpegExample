package com.zeek1910.ffmpegexample

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private val _images: MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
    val images: StateFlow<List<Uri>> = _images.asStateFlow()

    fun onImageAdded(images: List<Uri>) = viewModelScope.launch { _images.emit(images) }

    fun onButtonClearClicked() = viewModelScope.launch { _images.emit(emptyList()) }

}