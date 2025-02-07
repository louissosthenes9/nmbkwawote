package com.devt.NmbKwaWote.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.devt.NmbKwaWote.presentation.components.common.CustomTopAppBar

@Composable
fun HomeScreen(onSettingsClick: () -> Unit) {
    Scaffold(
        topBar = {
            // Pass the settings click lambda to your top app bar.
            CustomTopAppBar(
                userName = "kenneth kilimba",
                onSettingsClick = onSettingsClick
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Content of your home screen. For now, we display some placeholder text.
        Text(
            text = "Home Screen Content",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
