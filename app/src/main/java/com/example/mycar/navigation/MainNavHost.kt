package com.example.mycar.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.mycar.UserViewModel
import com.example.mycar.screen.*

@Composable
fun MainNavHost(
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val bottomItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Default.Home),
        BottomNavItem("vehicles", "Vehículos", Icons.Default.DirectionsCar),
        BottomNavItem("maintenance", "Mant.", Icons.Default.Build),
        BottomNavItem("expenses", "Gastos", Icons.Default.AttachMoney),
        BottomNavItem("profile", "Perfil", Icons.Default.Person),
    )

    Scaffold(
        bottomBar = {
            // ✅ aquí controlas en qué pantallas se muestra el bottom bar
            if (currentRoute in listOf("home", "vehicles", "maintenance", "expenses", "profile")) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp
                ) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.2f else 1f,
                            label = "icon_scale"
                        )

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) Color(0xFF1565C0) else Color.Gray,
                                    modifier = Modifier.scale(scale)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    color = if (selected) Color(0xFF1565C0) else Color.Gray
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            // LOGIN
            composable("login") {
                LoginScreen(
                    userViewModel = userViewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onGoRegister = { navController.navigate("register") }
                )
            }

            // REGISTRO
            composable("register") {
                RegisterScreen(
                    userViewModel = userViewModel,
                    onRegistered = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onGoLogin = { navController.popBackStack() }
                )
            }

            // HOME
            composable("home") {
                HomeScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }

            // VEHÍCULOS
            composable("vehicles") {
                ManageVehicleScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }

            // MANTENIMIENTOS
            composable("maintenance") {
                MaintenanceScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }

            // HISTORIAL DE MANTENIMIENTOS
            composable("maintenanceHistory") {
                MaintenanceHistoryScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }

            // GASTOS
            composable("expenses") {
                ExpenseScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }

            // HISTORIAL DE GASTOS
            composable("expenseHistory") {
                ExpenseHistoryScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }

            // PERFIL
            composable("profile") {
                ProfileScreen(
                    userViewModel = userViewModel,
                    navController = navController
                )
            }
        }
    }
}

// Modelo de ítem para la barra inferior
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
