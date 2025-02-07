package com.devt.NmbKwaWote.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.devt.NmbKwaWote.presentation.components.common.CustomBottomAppBar
import com.devt.NmbKwaWote.presentation.components.common.CustomTopAppBar
import com.devt.NmbKwaWote.presentation.components.common.NavItem
import com.devt.NmbKwaWote.presentation.navigation.NavGraph
import com.devt.NmbKwaWote.presentation.navigation.Screen
import com.devt.NmbKwaWote.presentation.theme.NmbKwaWoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NmbKwaWoteTheme {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }
}


@Composable
fun MainScreen(navController: androidx.navigation.NavHostController) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                userName = "louis kookaburra",
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        bottomBar = {
            CustomBottomAppBar(
                navItems = listOf(
                    NavItem("Home", Icons.Filled.Home),
                    NavItem("Transactions", Icons.Filled.Receipt),
                    NavItem("Card", Icons.Filled.CreditCard),
                    NavItem("Favourites", Icons.Filled.Star)
                ),
                onNavItemClick = { item ->
                    when (item.label) {
                        "Home" -> navController.navigate(Screen.Home.route) // Use Screen.Home.route
                        "Transactions" -> navController.navigate(Screen.Transactions.route)
                        "Card" -> navController.navigate(Screen.Card.route)
                        "Favourites" -> navController.navigate(Screen.Favourites.route)
                    }
                },
                onQrScanClick = { /* ... */ }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding to the content area
        ) {


            NavGraph(navController = navController)

        }
    }
}


