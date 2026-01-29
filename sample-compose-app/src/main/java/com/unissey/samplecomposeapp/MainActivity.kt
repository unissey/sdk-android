package com.unissey.samplecomposeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unissey.samplecomposeapp.ui.components.AppScaffold
import com.unissey.samplecomposeapp.ui.screens.VideoPlayerScreen
import com.unissey.samplecomposeapp.ui.theme.SampleAppTheme
import com.unissey.sdk.core.model.AcquisitionPreset.SelfieFast
import com.unissey.sdk.core.model.CameraEvent.VideoRecordEnded
import com.unissey.sdk.ui.domain.UnisseyViewModel
import com.unissey.sdk.ui.model.UnisseyPage
import com.unissey.sdk.ui.ui.screens.UnisseyScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var videoUri: String by rememberSaveable { mutableStateOf("") }

            val unisseyViewModel: UnisseyViewModel by viewModels {
                UnisseyViewModel.create(
                    SelfieFast
                )
            }

            LaunchedEffect(unisseyViewModel) {
                unisseyViewModel.cameraEvents.collect { event ->
                    if (event is VideoRecordEnded) {
                        val response = event.response

                        Log.d(
                            "UnisseySampleApp",
                            "Video record ended with file path: '${response.videoFilePath}'"
                        )
                        videoUri = response.videoFilePath
                    }
                }
            }

            SampleAppTheme {
                if (videoUri.isEmpty()) {
                    AppScaffold(
                        canNavigateUp = unisseyViewModel.currentPage != UnisseyPage.INSTRUCTIONS,
                        onUpClick = {
                            unisseyViewModel.navigateUp()
                        }) { contentPadding ->
                        UnisseyScreen(
                            cameraPermissionModifier = Modifier.padding(contentPadding),
                            unisseyViewModel = unisseyViewModel
                        )
                    }
                } else {
                    AppScaffold(
                        onUpClick = {
                            videoUri = ""
                        }) { contentPadding ->
                        VideoPlayerScreen(
                            modifier = Modifier.padding(contentPadding),
                            videoUri = videoUri,
                            onRestartButtonClick = {
                                unisseyViewModel.navigateUp()
                                videoUri = ""
                            }
                        )
                    }
                }
            }
        }
    }
}
