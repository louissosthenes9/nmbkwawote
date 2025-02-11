package com.devt.NmbKwaWote.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Transactions : Screen("transactions")
    object Card : Screen("card")
    object Favourites : Screen("favourites")
    object Auth : Screen("Login")
    object SendMoney : Screen("send_money")
    object WithdrawMoney : Screen("withdraw_money")
    object PayBills: Screen("pay_bills")

}