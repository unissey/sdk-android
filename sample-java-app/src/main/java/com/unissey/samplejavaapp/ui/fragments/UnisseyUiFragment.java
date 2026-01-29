package com.unissey.samplejavaapp.ui.fragments;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.FlowLiveDataConversions;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.unissey.samplejavaapp.R;
import com.unissey.samplejavaapp.ui.SharedViewModel;
import com.unissey.sdk.core.model.AcquisitionPreset.SelfieFast;
import com.unissey.sdk.core.model.CameraEvent.VideoRecordEnded;
import com.unissey.sdk.core.model.SessionResponse;
import com.unissey.sdk.ui.domain.UnisseyViewModel;
import com.unissey.sdk.ui.legacy.UnisseyViewKt;

public class UnisseyUiFragment extends Fragment {

    private SharedViewModel sharedViewModel;

    private UnisseyViewModel unisseyViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        unisseyViewModel = new ViewModelProvider(this,
                UnisseyViewModel.Factory.create(SelfieFast.INSTANCE)).get(UnisseyViewModel.class);

        FlowLiveDataConversions.asLiveData(unisseyViewModel.getCameraEvents()).observe(getViewLifecycleOwner(), event -> {
            if (event instanceof VideoRecordEnded) {
                SessionResponse response = ((VideoRecordEnded) event).getResponse();
                Log.d(
                        "UnisseyUiFragment",
                        "Video record ended with file path: " + response.getVideoFilePath()
                );
                sharedViewModel.setVideoUri(response.getVideoFilePath());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_unisseyUiFragment_to_videoPlayerFragment);
            }
        });

        FlowLiveDataConversions.asLiveData(unisseyViewModel.getErrorEvents()).observe(getViewLifecycleOwner(), event -> {
            Log.e(
                    "UnisseyUiFragment",
                    "Received an ErrorEvent " + event.getMessage()
            );
            Snackbar.make(
                    requireView(),
                    event.getMessage(),
                    Snackbar.LENGTH_SHORT
            ).show();
            findNavController(requireView()).navigateUp();
        });

        return UnisseyViewKt.getUnisseyView(requireContext(), unisseyViewModel);
    }

    public boolean navigateUp() {
        return unisseyViewModel.navigateUp();
    }
}