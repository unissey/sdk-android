package com.unissey.samplejavaapp.ui.fragments;

import static androidx.navigation.Navigation.findNavController;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.FlowLiveDataConversions;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.unissey.samplejavaapp.R;
import com.unissey.samplejavaapp.databinding.FragmentUnisseyHeadlessBinding;
import com.unissey.samplejavaapp.ui.SharedViewModel;
import com.unissey.sdk.core.domain.UnisseySession;
import com.unissey.sdk.core.model.AcquisitionPreset.SelfieFast;
import com.unissey.sdk.core.model.CameraEvent.CameraReady;
import com.unissey.sdk.core.model.CameraEvent.VideoCaptureStarted;
import com.unissey.sdk.core.model.CameraEvent.VideoRecordEnded;
import com.unissey.sdk.core.model.CameraEvent.VideoRecordProgress;
import com.unissey.sdk.core.model.SessionResponse;
import com.unissey.sdk.core.utils.PreviewUtilsKt;

import kotlin.Unit;

public class UnisseyHeadlessFragment extends Fragment {

    private FragmentUnisseyHeadlessBinding binding;

    private SharedViewModel sharedViewModel;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            startCamera();
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Camera permission denied",
                                    Toast.LENGTH_SHORT
                            ).show();
                            NavHostFragment.findNavController(this).navigateUp();
                        }
                    }
            );

    public UnisseyHeadlessFragment() {
        super(R.layout.fragment_unissey_headless);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentUnisseyHeadlessBinding.bind(view);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if (sharedViewModel.unisseySession == null) {
            sharedViewModel.unisseySession = new UnisseySession(SelfieFast.INSTANCE);
        }

        // Show the loading screen if the camera hasn't started yet, doesn't show on configuration changes
        if (!sharedViewModel.isCameraStarted) {
            binding.cameraLoadingContainer.setVisibility(View.VISIBLE);
            binding.unisseyHeadlessGroup.setVisibility(View.INVISIBLE);
        }

        // Update the UI on configuration changes if the capture is already running
        if (sharedViewModel.isVideoCaptureRunning) {
            binding.startButton.setEnabled(false);
            binding.instructionText.setText(sharedViewModel.instructionText.getValue());
        }

        FlowLiveDataConversions.asLiveData(sharedViewModel.unisseySession.getCameraEvents()).observe(getViewLifecycleOwner(), event -> {
            if (event instanceof CameraReady) {
                Size previewResolution = ((CameraReady) event).getPreviewResolution() != null ? ((CameraReady) event).getPreviewResolution() : new Size(720, 1280);
                RectF faceArea = PreviewUtilsKt.getFaceAreaInPreview(
                        previewResolution,
                        new Size(binding.previewView.getWidth(), binding.previewView.getHeight()),
                        binding.previewView.getScaleType()
                );

                binding.overlayView.setFaceArea(faceArea);
                binding.overlayView.setRecordingProgress(0);

                // Show the preview interface
                binding.cameraLoadingContainer.setVisibility(View.GONE);
                binding.unisseyHeadlessGroup.setVisibility(View.VISIBLE);
            }

            if (event instanceof VideoCaptureStarted) {
                sharedViewModel.isVideoCaptureRunning = true;
            }

            if (event instanceof VideoRecordProgress) {
                binding.overlayView.setRecordingProgress(((VideoRecordProgress) event).getProgress());
                binding.overlayView.invalidate();
            }

            if (event instanceof VideoRecordEnded) {
                SessionResponse response = ((VideoRecordEnded) event).getResponse();
                Log.d(
                        "UnisseyHeadlessFragment",
                        "Video record ended with file path: " + response.getVideoFilePath()
                );
                sharedViewModel.setVideoUri(response.getVideoFilePath());
                findNavController(requireView())
                        .navigate(R.id.action_unisseyHeadlessFragment_to_videoPlayerFragment);
            }
        });

        FlowLiveDataConversions.asLiveData(sharedViewModel.unisseySession.getFaceDetectionEvents()).observe(getViewLifecycleOwner(), event -> {
            switch (event.getFaceDetectionInfo()) {
                case NO_FACES_DETECTED:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_no_face_detected));
                    break;
                case TOO_MANY_FACES:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_multiple_faces_detected));
                    break;
                case FACE_TOO_CLOSE:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_get_further_away));
                    break;
                case FACE_TOO_FAR:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_get_closer));
                    break;
                case MOVE_LEFT:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_left));
                    break;
                case MOVE_RIGHT:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_right));
                    break;
                case MOVE_UP:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_up));
                    break;
                case MOVE_DOWN:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_down));
                    break;
                case GOOD_POSITION:
                    sharedViewModel.instructionText.setValue(getResources().getString(com.unissey.sdk.ui.R.string.unissey_instruction_do_not_move));
                    break;
            }
        });

        FlowLiveDataConversions.asLiveData(sharedViewModel.unisseySession.getErrorEvents()).observe(getViewLifecycleOwner(), event -> {
            Log.e(
                    "UnisseyHeadlessFragment",
                    "Received an ErrorEvent " + event.getMessage()
            );
            Snackbar.make(
                    requireView(),
                    event.getMessage(),
                    Snackbar.LENGTH_SHORT
            ).show();
            stopCamera();
            findNavController(requireView()).navigateUp();
        });

        binding.startButton.setOnClickListener(button -> {
            button.setEnabled(false);
            sharedViewModel.unisseySession.startVideoCapture(requireContext());
        });

        sharedViewModel.instructionText.observe(getViewLifecycleOwner(),
                text -> binding.instructionText.setText(text)
        );

        if (hasCameraPermission()) {
            startCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving() && !requireActivity().isChangingConfigurations()) {
            stopCamera();
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        sharedViewModel.unisseySession.startCamera(
                requireContext(),
                getViewLifecycleOwner(),
                () -> {
                    sharedViewModel.unisseySession.bindPreview(binding.previewView);
                    sharedViewModel.isCameraStarted = true;
                    return Unit.INSTANCE;
                }
        );
    }

    private void stopCamera() {
        if (sharedViewModel.unisseySession != null) {
            sharedViewModel.unisseySession.stopCamera();
        }
        sharedViewModel.unisseySession = null;
        sharedViewModel.isCameraStarted = false;
        sharedViewModel.isVideoCaptureRunning = false;
    }
}