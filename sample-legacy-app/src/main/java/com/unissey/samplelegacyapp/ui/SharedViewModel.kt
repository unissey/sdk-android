package com.unissey.samplelegacyapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SharedViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val videoUri = savedStateHandle.getLiveData<String>("videoUri")

    fun setVideoUri(videoUri: String) {
        savedStateHandle["videoUri"] = videoUri
    }

}