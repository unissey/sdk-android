package com.unissey.samplelegacyapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.unissey.samplelegacyapp.R
import com.unissey.samplelegacyapp.ui.SharedViewModel
import com.unissey.sdk.core.model.AcquisitionPreset.SelfieFast
import com.unissey.sdk.core.model.CameraEvent.VideoRecordEnded
import com.unissey.sdk.ui.domain.UnisseyViewModel
import com.unissey.sdk.ui.ui.screens.UnisseyScreen
import kotlinx.coroutines.launch

class UnisseyUiFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val unisseyViewModel: UnisseyViewModel by viewModels {
        UnisseyViewModel.create(SelfieFast)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                unisseyViewModel.cameraEvents.collect { event ->
                    if (event is VideoRecordEnded) {
                        val response = event.response
                        Log.d(
                            "UnisseyUiFragment",
                            "Video record ended with file path: '${response.videoFilePath}"
                        )
                        sharedViewModel.setVideoUri(response.videoFilePath)
                        requireView().findNavController()
                            .navigate(R.id.action_unisseyUiFragment_to_videoPlayerFragment)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                unisseyViewModel.errorEvents.collect { event ->
                    Log.e(
                        "UnisseyUiFragment",
                        "Received an ErrorEvent: ${event.message}"
                    )
                    Snackbar.make(
                        requireView(),
                        event.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    requireView().findNavController().navigateUp()
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                UnisseyScreen(
                    unisseyViewModel = unisseyViewModel,
                )
            }
        }
    }

    fun navigateUp(): Boolean {
        return unisseyViewModel.navigateUp()
    }

}