package com.example.inventarioacademico.ui.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(viewModel: InventarioViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("📊") },
                    label = { Text("Dashboard") }
                )

                NavigationBarItem(
                    selected = currentRoute == "equipos",
                    onClick = {
                        navController.navigate("equipos") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("💻") },
                    label = { Text("Equipos") }
                )

                NavigationBarItem(
                    selected = currentRoute == "prestamos",
                    onClick = {
                        navController.navigate("prestamos") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("🤝") },
                    label = { Text("Préstamos") }
                )
            }
        }
    ) { paddingValues ->
        // Aquí se decide qué pantalla mostrar según la ruta
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("equipos") { EquiposScreen(viewModel) }
            composable("prestamos") { PrestamosScreen(viewModel) }
        }
    }
}