package com.example.mycar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val alerts = userViewModel.alerts
    val gradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))

    // Cargar alertas al entrar
    LaunchedEffect(Unit) { userViewModel.loadAlerts() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Alertas",
                        fontWeight = FontWeight.Bold,
                        color = MyCarBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = MyCarBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                if (alerts.isEmpty()) {
                    //  Sin alertas
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tienes alertas activas",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    // Lista de alertas
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(alerts.size) { index ->
                            val alert = alerts[index]

                            // Tipo de ícono y color según alerta
                            val (icon, color) = when {
                                alert.title.contains("SOAP", true) ->
                                    Icons.Filled.Warning to Color(0xFFFFA726)
                                alert.title.contains("Permiso", true) ->
                                    Icons.Filled.Description to Color(0xFF42A5F5)
                                alert.title.contains("Revisión", true) ->
                                    Icons.Filled.Build to Color(0xFF66BB6A)
                                else -> Icons.Filled.Notifications to MyCarBlue
                            }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            alert.title,
                                            color = MyCarBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(alert.message, color = Color.DarkGray)

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        " ${alert.date}",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.labelSmall
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = { userViewModel.removeAlert(alert) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
