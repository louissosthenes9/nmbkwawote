package com.devt.NmbKwaWote.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devt.NmbKwaWote.presentation.screens.HomeScreen
import com.devt.NmbKwaWote.presentation.screens.SettingScreen
import com.devt.NmbKwaWote.presentation.theme.NmbKwaWoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Optional: draws your content edge-to-edge.
        setContent {
            NmbKwaWoteTheme {
                // Create a NavController to handle navigation between screens.
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Set up the NavHost with a start destination of "home"
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // Home screen composable
                        composable("home") {
                            HomeScreen(
                                onSettingsClick = {
                                    // Navigate to the settings screen when the settings button is clicked.
                                    navController.navigate("settings")
                                }
                            )
                        }
                        // Settings screen composable
                        composable("settings") {
                            SettingScreen()
                        }
                    }
                }
            }
        }
    }
}
