package com.effitrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.effitrack.data.local.UserSession
import com.effitrack.ui.screens.login.LoginScreen
import com.effitrack.ui.screens.main.MainScreen
import com.effitrack.util.Constants.ROUTE_LOGIN
import com.effitrack.util.Constants.ROUTE_MAIN

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val startDestination = if (UserSession.isLoggedIn) {
        ROUTE_MAIN
    } else {
        ROUTE_LOGIN
    }


    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(ROUTE_MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}