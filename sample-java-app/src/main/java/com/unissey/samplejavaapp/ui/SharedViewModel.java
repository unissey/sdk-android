package com.unissey.samplejavaapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.unissey.sdk.core.domain.UnisseySession;

public class SharedViewModel extends ViewModel {

    private final SavedStateHandle state;

    public LiveData<String> getVideoUri() {
        return state.getLiveData("videoUri");
    }

    public void setVideoUri(String videoUri) {
        state.set("videoUri", videoUri);
    }

    public UnisseySession unisseySession = null;
    public boolean isCameraStarted = false;
    public boolean isVideoCaptureRunning = false;
    public MutableLiveData<String> instructionText = new MutableLiveData<>();

    public SharedViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }
}
