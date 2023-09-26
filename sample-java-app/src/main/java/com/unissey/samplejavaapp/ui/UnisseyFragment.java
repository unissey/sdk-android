package com.unissey.samplejavaapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.unissey.samplejavaapp.R;
import com.unissey.sdk.legacy.OnRecordEndedListener;
import com.unissey.sdk.legacy.UnisseyViewKt;
import com.unissey.sdk.model.SelfieFast;
import com.unissey.sdk.model.SessionResponse;
import com.unissey.sdk.model.SessionResult;
import com.unissey.sdk.ui.screens.UnisseyViewModel;

public class UnisseyFragment extends Fragment {

    private SharedViewModel sharedViewModel;

    private final OnRecordEndedListener onRecordEndedListener = (SessionResult result) -> {
        SessionResponse response = result.getOrThrow();
        Log.d(
                "UnisseyDemo",
                "Video record ended with file path: " + response.getVideoFilePath()
        );
        sharedViewModel.setVideoUri(response.getVideoFilePath());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_unisseyFragment_to_videoPlayerFragment);
    };

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        UnisseyViewModel unisseyViewModel =
                new ViewModelProvider(this,
                        UnisseyViewModel.Factory.create(SelfieFast.INSTANCE,
                                onRecordEndedListener)).get(UnisseyViewModel.class);

        // Update the OnRecordEndedListener since the reference to SharedViewModel has changed in
        // case of a Fragment recreation (it happens upon configuration changes such as a change
        // in the screen orientation)
        unisseyViewModel.setOnRecordEndedListener(onRecordEndedListener);

        return UnisseyViewKt.getUnisseyView(requireContext(), unisseyViewModel);
    }
}