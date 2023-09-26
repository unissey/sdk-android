package com.unissey.samplejavaapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final SavedStateHandle state;

    public LiveData<String> getVideoUri() {
        return state.getLiveData("videoUri");
    }

    public void setVideoUri(String videoUri) {
        state.set("videoUri", videoUri);
    }

    public SharedViewModel(SavedStateHandle savedStateHandle) {
        state = savedStateHandle;
    }
}
