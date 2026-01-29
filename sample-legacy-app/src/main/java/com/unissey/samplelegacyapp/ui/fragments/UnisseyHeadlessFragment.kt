package com.unissey.samplelegacyapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.unissey.samplelegacyapp.R
import com.unissey.samplelegacyapp.databinding.FragmentUnisseyHeadlessBinding
import com.unissey.samplelegacyapp.ui.SharedViewModel
import com.unissey.sdk.core.domain.UnisseySession
import com.unissey.sdk.core.model.AcquisitionPreset.SelfieFast
import com.unissey.sdk.core.model.CameraEvent.CameraReady
import com.unissey.sdk.core.model.CameraEvent.VideoCaptureStarted
import com.unissey.sdk.core.model.CameraEvent.VideoRecordEnded
import com.unissey.sdk.core.model.CameraEvent.VideoRecordProgress
import com.unissey.sdk.core.model.FaceDetectionInfo
import com.unissey.sdk.core.utils.getFaceAreaInPreview
import kotlinx.coroutines.launch

class UnisseyHeadlessFragment : Fragment(R.layout.fragment_unissey_headless) {

    private var _binding: FragmentUnisseyHeadlessBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(), "Camera permission denied", Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUnisseyHeadlessBinding.bind(view)

        if (sharedViewModel.unisseySession == null) {
            sharedViewModel.unisseySession = UnisseySession(SelfieFast)
        }

        // Show the loading screen if the camera hasn't started yet, doesn't show on configuration changes
        if (!sharedViewModel.isCameraStarted) {
            binding.cameraLoadingContainer.visibility = View.VISIBLE
            binding.unisseyHeadlessGroup.visibility = View.INVISIBLE
        }

        // Update the UI on configuration changes if the capture is already running
        if (sharedViewModel.isVideoCaptureRunning) {
            binding.startButton.isEnabled = false
            binding.instructionText.text = sharedViewModel.instructionText.value
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.unisseySession?.cameraEvents?.collect { event ->
                    if (event is CameraReady) {
                        binding.overlayView.apply {
                            faceArea = getFaceAreaInPreview(
                                event.previewResolution ?: Size(720, 1280),
                                Size(
                                    binding.previewView.width, binding.previewView.height
                                ),
                                binding.previewView.scaleType,
                            )

                            recordingProgress = 0f
                        }

                        // Show the preview interface
                        binding.cameraLoadingContainer.visibility = View.GONE
                        binding.unisseyHeadlessGroup.visibility = View.VISIBLE
                    }

                    if (event is VideoCaptureStarted) {
                        sharedViewModel.isVideoCaptureRunning = true
                    }

                    if (event is VideoRecordProgress) {
                        // Update the progress indicator
                        binding.overlayView.recordingProgress = event.progress
                        binding.overlayView.invalidate()
                    }

                    if (event is VideoRecordEnded) {
                        val response = event.response
                        Log.d(
                            "UnisseyHeadlessFragment",
                            "Video record ended with file path: '${response.videoFilePath}"
                        )
                        sharedViewModel.setVideoUri(response.videoFilePath)
                        findNavController().navigate(R.id.action_unisseyHeadlessFragment_to_videoPlayerFragment)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.unisseySession?.faceDetectionEvents?.collect { event ->
                    sharedViewModel.instructionText.value = when (event.faceDetectionInfo) {
                        FaceDetectionInfo.NO_FACES_DETECTED -> resources.getString(
                            com.unissey.sdk.ui.R.string.unissey_instruction_no_face_detected
                        )

                        FaceDetectionInfo.TOO_MANY_FACES -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_multiple_faces_detected)
                        FaceDetectionInfo.FACE_TOO_CLOSE -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_get_further_away)
                        FaceDetectionInfo.FACE_TOO_FAR -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_get_closer)
                        FaceDetectionInfo.MOVE_LEFT -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_left)
                        FaceDetectionInfo.MOVE_RIGHT -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_right)
                        FaceDetectionInfo.MOVE_UP -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_up)
                        FaceDetectionInfo.MOVE_DOWN -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_move_down)
                        FaceDetectionInfo.GOOD_POSITION -> resources.getString(com.unissey.sdk.ui.R.string.unissey_instruction_do_not_move)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.unisseySession?.errorEvents?.collect { event ->
                    Log.e(
                        "UnisseyHeadlessFragment",
                        "Received an ErrorEvent: ${event.message}"
                    )
                    Snackbar.make(
                        requireView(),
                        event.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    stopCamera()
                    findNavController().navigateUp()
                }
            }
        }

        binding.startButton.setOnClickListener {
            it.isEnabled = false
            sharedViewModel.unisseySession?.startVideoCapture(requireContext())
        }

        sharedViewModel.instructionText.observe(viewLifecycleOwner) {
            binding.instructionText.text = it
        }

        if (!hasCameraPermission()) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRemoving && !requireActivity().isChangingConfigurations) {
            stopCamera()
        }
    }

    private fun hasCameraPermission(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.unisseySession?.startCamera(requireContext(), viewLifecycleOwner)
            sharedViewModel.unisseySession?.bindPreview(binding.previewView)
            sharedViewModel.isCameraStarted = true
        }
    }

    private fun stopCamera() {
        sharedViewModel.unisseySession?.stopCamera()
        sharedViewModel.unisseySession = null
        sharedViewModel.isCameraStarted = false
        sharedViewModel.isVideoCaptureRunning = false
    }
}