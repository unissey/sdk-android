package com.unissey.samplecomposeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unissey.samplecomposeapp.ui.components.AppScaffold
import com.unissey.samplecomposeapp.ui.screens.VideoPlayerScreen
import com.unissey.samplecomposeapp.ui.theme.SampleAppTheme
import com.unissey.sdk.model.SelfieFast
import com.unissey.sdk.model.UnisseyPage
import com.unissey.sdk.ui.screens.UnisseyScreen
import com.unissey.sdk.ui.screens.UnisseyViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var videoUri: String by rememberSaveable { mutableStateOf("") }

            val unisseyViewModel: UnisseyViewModel by viewModels {
                UnisseyViewModel.Factory.create(
                    SelfieFast
                ) { result ->
                    val response = result.getOrThrow()

                    Log.d(
                        "UnisseySampleApp",
                        "Video record ended with file path: '${response.videoFilePath}'"
                    )
                    videoUri = response.videoFilePath
                }
            }

            SampleAppTheme {
                if (videoUri.isEmpty()) {
                    AppScaffold(
                        canNavigateUp = unisseyViewModel.currentPage != UnisseyPage.INSTRUCTIONS,
                        onUpClicked = {
                            unisseyViewModel.navigateUp()
                        }) { contentPadding ->
                        UnisseyScreen(
                            cameraPermissionModifier = Modifier.padding(contentPadding),
                            unisseyViewModel = unisseyViewModel
                        )
                    }
                } else {
                    AppScaffold(
                        onUpClicked = {
                            videoUri = ""
                        }) { contentPadding ->
                        VideoPlayerScreen(
                            modifier = Modifier.padding(contentPadding),
                            videoUri = videoUri,
                            onRestartButtonClicked = {
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
