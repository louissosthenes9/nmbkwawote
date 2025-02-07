package com.devt.NmbKwaWote.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Transactions : Screen("transactions")
    object Card : Screen("card")
    object Favourites : Screen("favourites")
}