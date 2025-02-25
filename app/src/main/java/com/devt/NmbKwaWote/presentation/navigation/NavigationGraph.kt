package com.devt.NmbKwaWote.presentation.navigation

import CardScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devt.NmbKwaWote.presentation.HomeContent
import com.devt.NmbKwaWote.presentation.screens.FavouritesScreen
import com.devt.NmbKwaWote.presentation.screens.LoginScreen
import com.devt.NmbKwaWote.presentation.screens.SettingScreen
import com.devt.NmbKwaWote.presentation.screens.TransactionsScreen
import com.devt.NmbKwaWote.presentation.screens.features.PayBillScreen
import com.devt.NmbKwaWote.presentation.screens.features.SendMoneyScreen
import com.devt.NmbKwaWote.presentation.screens.features.WithdrawMoneyScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            LoginScreen()
        }

        composable(Screen.Home.route) {
            HomeContent(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingScreen(navController = navController)
        }

        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
        }

        composable(Screen.Card.route) {
            CardScreen(navController = navController)
        }

        composable(Screen.Favourites.route) {
            FavouritesScreen(navController = navController)
        }

        composable(Screen.SendMoney.route){
            SendMoneyScreen(navController = navController)
        }

        composable(Screen.PayBills.route){
            PayBillScreen(navController = navController)
        }

        composable(Screen.WithdrawMoney.route){
            WithdrawMoneyScreen(navController = navController)
        }
    }
}