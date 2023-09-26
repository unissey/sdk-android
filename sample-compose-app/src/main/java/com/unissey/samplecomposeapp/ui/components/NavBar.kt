package com.unissey.samplecomposeapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    canNavigateUp: Boolean = true,
    onUpClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            NavBar(canNavigateUp = canNavigateUp) { onUpClicked() }
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavBar(
    canNavigateUp: Boolean,
    onUpClicked: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(onClick = { onUpClicked() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(
                            id = R.string.back
                        )
                    )
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}