package com.unissey.samplelegacyapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.unissey.sdk.core.domain.UnisseySession

class SharedViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val videoUri = savedStateHandle.getLiveData<String>("videoUri")
    var isCameraStarted = false
    var isVideoCaptureRunning = false
    var instructionText = MutableLiveData<String>()
    var unisseySession: UnisseySession? = null

    fun setVideoUri(videoUri: String) {
        savedStateHandle["videoUri"] = videoUri
    }
}