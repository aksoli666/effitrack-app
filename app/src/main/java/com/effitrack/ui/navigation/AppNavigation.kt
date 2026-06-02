package com.effitrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.effitrack.data.local.UserSession
import com.effitrack.ui.screens.login.LoginScreen
import com.effitrack.ui.screens.main.MainScreen
import com.effitrack.ui.screens.status.EffiTrackUniversalStatusScreen
import com.effitrack.util.Constants.EMPTY_STRING
import com.effitrack.util.Constants.ROUTE_LOGIN
import com.effitrack.util.Constants.ROUTE_MAIN
import com.effitrack.util.Constants.ROUTE_UNIVERSAL_STATUS

@Composable
fun AppNavigation(
    openStatusScreen: Boolean = false,
    initialErrorMessage: String = EMPTY_STRING,
    onStatusHandled: () -> Unit = {}
) {
    val navController = rememberNavController()

    var savedErrorMessage by remember { mutableStateOf(EMPTY_STRING) }

    if (initialErrorMessage.isNotEmpty()) {
        savedErrorMessage = initialErrorMessage
    }

    LaunchedEffect(openStatusScreen, initialErrorMessage) {
        if (openStatusScreen && initialErrorMessage.isNotEmpty()) {
            navController.navigate(ROUTE_UNIVERSAL_STATUS) {
                popUpTo(0) { inclusive = true }
            }
            onStatusHandled()
        }
    }

    val startDestination = if (UserSession.isLoggedIn) ROUTE_MAIN else ROUTE_LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    UserSession.clear()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(ROUTE_UNIVERSAL_STATUS) {
            EffiTrackUniversalStatusScreen(
                errorMessage = savedErrorMessage,
                onRetryClick = {
                    // Перенаправляем обратно в зависимости от того, залогинен ли юзер
                    val destination = if (UserSession.isLoggedIn) ROUTE_MAIN else ROUTE_LOGIN
                    navController.navigate(destination) {
                        popUpTo(ROUTE_UNIVERSAL_STATUS) { inclusive = true }
                    }
                }
            )
        }
    }
}