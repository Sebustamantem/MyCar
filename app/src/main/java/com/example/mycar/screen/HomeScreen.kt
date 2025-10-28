package com.example.mycar.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    //  Datos del usuario y vehículos
    val userName by userViewModel.userName
    val vehicles = userViewModel.vehicles
    val mainVehicle = vehicles.firstOrNull()

    // Cargar vehículos al iniciar
    LaunchedEffect(Unit) {
        userViewModel.loadVehicles()
    }

    //  Fondo con degradado
    val backgroundBrush = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White
    val textColor = Color.Black
    val secondaryText = Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  Tarjeta principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Bienvenida
                    Text(
                        text = "Hola, ${if (userName.isNotEmpty()) userName else "Usuario"} ",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tu vehículo principal:",
                        color = secondaryText,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Datos del vehículo principal
                    AnimatedVisibility(visible = mainVehicle != null) {
                        Column {
                            Text(
                                text = "${mainVehicle?.brand} ${mainVehicle?.model} (${mainVehicle?.year})",
                                fontWeight = FontWeight.Bold,
                                color = MyCarBlue,
                                fontSize = 18.sp
                            )
                            Text("Patente: ${mainVehicle?.plate}", color = secondaryText)
                            Text("Km actuales: ${mainVehicle?.km} km", color = secondaryText)
                        }
                    }

                    // Si no hay vehículo
                    AnimatedVisibility(visible = mainVehicle == null) {
                        Text(
                            text = "Aún no has agregado ningún vehículo.",
                            color = secondaryText,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Fila 1: Gestionar / Historial
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ElevatedButton(
                            onClick = { navController.navigate("vehicles") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = "Vehículos", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gestionar", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("maintenanceHistory") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.History, contentDescription = "Historial")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Historial")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fila 2: Mantenimientos / Alertas
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("maintenance") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Build, contentDescription = "Mantenimientos")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mantenimientos")
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("alerts") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Alertas")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alertas")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //  Botón centrado: Perfil
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("profile") },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "Perfil")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Perfil")
                        }
                    }
                }
            }
        }
    }
}
