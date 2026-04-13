package com.effitrack.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.effitrack.ui.reusingComponents.BottomBar
import com.effitrack.ui.screens.equipment.EquipmentListScreen
import com.effitrack.ui.screens.profile.ProfileScreen
import com.effitrack.ui.theme.Dimens
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.effitrack.ui.screens.equipment.details.EquipmentDetailBottomSheet
import com.effitrack.ui.screens.scanner.ScannerScreen
import com.effitrack.util.Constants.PARAM_ID
import com.effitrack.util.Constants.ROUTE_DASHBOARD
import com.effitrack.util.Constants.ROUTE_EQUIPMENT_DETAILS
import com.effitrack.util.Constants.ROUTE_PROFILE
import com.effitrack.util.Constants.ROUTE_SCANNER

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedIndex = remember(currentRoute) {
        when (currentRoute) {
            ROUTE_DASHBOARD -> 0
            ROUTE_PROFILE -> 1
            ROUTE_SCANNER -> 2
            else -> -1
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = ROUTE_DASHBOARD,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(ROUTE_DASHBOARD) {
                EquipmentListScreen(
                    onItemClick = { equipmentId ->
                        navController.navigate("equipment_details/$equipmentId")
                    }
                )
            }

            composable(ROUTE_PROFILE) {
                ProfileScreen(onLogout = onLogout)
            }

            composable(
                route = ROUTE_EQUIPMENT_DETAILS,
                arguments = listOf(navArgument(PARAM_ID) { type = NavType.StringType })
            ) {
                EquipmentDetailBottomSheet(
                    onDismissRequest = { navController.popBackStack() }
                )
            }

            composable(ROUTE_SCANNER) {
                ScannerScreen(
                    onScanSuccess = {},
                )
            }
        }

        if (currentRoute != ROUTE_EQUIPMENT_DETAILS) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens.spaceSmall)
                    .systemBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                BottomBar(
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        val route = when (index) {
                            0 -> ROUTE_DASHBOARD
                            1 -> ROUTE_PROFILE
                            2 -> ROUTE_SCANNER
                            else -> ROUTE_PROFILE
                        }

                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}