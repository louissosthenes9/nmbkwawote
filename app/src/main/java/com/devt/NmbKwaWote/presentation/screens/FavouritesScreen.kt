package com.devt.NmbKwaWote.presentation.screens

import CommonScreenLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
@Composable
fun FavouritesScreen(navController: NavHostController) {
    CommonScreenLayout(
        navController = navController,
        screenTitle = "Favourites"
    ) { paddingValues ->
        // Your favourites screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Favourite screen")
        }
    }
}