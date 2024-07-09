package com.unissey.samplecomposeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.unissey.samplecomposeapp.R

@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    videoUri: String,
    onRestartButtonClick: () -> Unit
) {
    val context = LocalContext.current

    val exoPlayer = ExoPlayer.Builder(LocalContext.current)
        .build()
        .also { exoPlayer ->
            val mediaItem = MediaItem.Builder()
                .setUri(videoUri)
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints {
            val maxHeight = maxHeight

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    modifier = Modifier.height(maxHeight * 0.75f),
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                        }
                    }
                )
                Button(onClick = onRestartButtonClick) {
                    Text(
                        text = stringResource(R.string.restart).uppercase(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
