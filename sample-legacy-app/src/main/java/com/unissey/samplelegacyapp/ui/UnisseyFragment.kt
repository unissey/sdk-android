package com.unissey.samplelegacyapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.unissey.samplelegacyapp.R
import com.unissey.sdk.model.SelfieFast
import com.unissey.sdk.ui.screens.OnRecordEndedListener
import com.unissey.sdk.ui.screens.UnisseyScreen
import com.unissey.sdk.ui.screens.UnisseyViewModel

class UnisseyFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val onRecordEndedListener: OnRecordEndedListener = { result ->
        val data = result.getOrThrow()
        Log.d(
            "UnisseyDemo",
            "Video record ended with file path: '${data.videoFilePath}"
        )
        sharedViewModel.setVideoUri(data.videoFilePath)
        Navigation.findNavController(requireView())
            .navigate(R.id.action_unisseyFragment_to_videoPlayerFragment)
    }

    private val unisseyViewModel: UnisseyViewModel by viewModels {
        UnisseyViewModel.Factory.create(
            SelfieFast,
            onRecordEndedListener = onRecordEndedListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Update the OnRecordEndedListener since the reference to SharedViewModel has changed in
        // case of a Fragment recreation (it happens upon configuration changes such as a change
        // in the screen orientation)
        unisseyViewModel.onRecordEndedListener = onRecordEndedListener

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                UnisseyScreen(
                    instructionsModifier = Modifier.padding(top = 56.dp),
                    cameraPermissionModifier = Modifier.padding(top = 56.dp),
                    unisseyViewModel = unisseyViewModel,
                )
            }
        }
    }

}