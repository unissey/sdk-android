package com.unissey.samplecomposeapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.unissey.samplecomposeapp.R

@Composable
fun AppScaffold(
    canNavigateUp: Boolean = true,
    onUpClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            NavBar(canNavigateUp = canNavigateUp) { onUpClick() }
        },
        content = content
    )
}

@Composable
private fun NavBar(
    canNavigateUp: Boolean,
    onUpClick: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(onClick = onUpClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(
                            id = R.string.back
                        )
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}