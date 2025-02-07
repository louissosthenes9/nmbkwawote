package com.devt.NmbKwaWote.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devt.NmbKwaWote.presentation.screens.* // Import your screens

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) }) }
        composable(Screen.Settings.route) { SettingScreen() }
        composable(Screen.Transactions.route) { TransactionsScreen() }
        composable(Screen.Card.route) { CardScreen() }
        composable(Screen.Favourites.route) { FavouritesScreen() }

    }
}