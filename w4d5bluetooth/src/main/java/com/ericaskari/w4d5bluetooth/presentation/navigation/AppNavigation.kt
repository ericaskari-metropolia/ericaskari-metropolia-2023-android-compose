package com.ericaskari.w4d5bluetooth.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ericaskari.w4d5bluetooth.presentation.pages.DeviceDetailsPage
import com.ericaskari.w4d5bluetooth.presentation.pages.DeviceServiceCharacteristicDetailsPage
import com.ericaskari.w4d5bluetooth.presentation.pages.DeviceServiceDetailsPage
import com.ericaskari.w4d5bluetooth.presentation.pages.DevicesPage

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "devices") {
        composable("devices") { backStackEntry ->
            DevicesPage(navController)
        }
        composable(
            "devices/{deviceId}",
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType }),
        ) { backStackEntry ->
            DeviceDetailsPage(navController, backStackEntry.arguments?.getString("deviceId"))
        }
        composable(
            "devices/{deviceId}/services/{serviceId}",
            arguments = listOf(
                navArgument("deviceId") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            DeviceServiceDetailsPage(
                navHostController = navController,
                deviceId = backStackEntry.arguments?.getString("deviceId"),
                serviceId = backStackEntry.arguments?.getString("serviceId")
            )
        }
        composable(
            "devices/{deviceId}/services/{serviceId}/characteristic/{characteristicId}",
            arguments = listOf(
                navArgument("deviceId") { type = NavType.StringType },
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("characteristicId") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            DeviceServiceCharacteristicDetailsPage(
                navHostController = navController,
                deviceId = backStackEntry.arguments?.getString("deviceId"),
                serviceId = backStackEntry.arguments?.getString("serviceId"),
                characteristicId = backStackEntry.arguments?.getString("characteristicId"),
            )
        }
    }
}