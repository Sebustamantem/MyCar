package com.example.mycar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue
import com.example.mycar.ui.theme.MyCarRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceHistoryScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val maintenanceList = userViewModel.maintenanceList
    val vehicles = userViewModel.vehicles

    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val secondaryText = Color.Gray
    val cardColor = Color.White

    var selectedVehicle by remember { mutableStateOf("") }
    var expandedVehicle by remember { mutableStateOf(false) }

    // Filtrar historial por vehículo
    val filteredRecords = if (selectedVehicle.isBlank()) {
        maintenanceList
    } else {
        maintenanceList.filter { it.vehiclePlate == selectedVehicle }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Text(
                text = "Historial de Mantenimientos",
                fontWeight = FontWeight.Bold,
                color = MyCarBlue,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filtro por vehículo
            ExposedDropdownMenuBox(
                expanded = expandedVehicle,
                onExpandedChange = { expandedVehicle = !expandedVehicle }
            ) {
                OutlinedTextField(
                    value = if (selectedVehicle.isEmpty()) "Todos los vehículos" else selectedVehicle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por vehículo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehicle) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedVehicle,
                    onDismissRequest = { expandedVehicle = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos los vehículos") },
                        onClick = {
                            selectedVehicle = ""
                            expandedVehicle = false
                        }
                    )

                    vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = { Text("${vehicle.brand} ${vehicle.model} (${vehicle.plate})") },
                            onClick = {
                                selectedVehicle = vehicle.plate
                                expandedVehicle = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de registros
            if (filteredRecords.isEmpty()) {
                Text(
                    text = "No hay mantenimientos registrados.",
                    color = secondaryText,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredRecords.size) { index ->
                        val record = filteredRecords[index]

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    text = record.type,
                                    fontWeight = FontWeight.Bold,
                                    color = MyCarBlue,
                                    fontSize = 18.sp
                                )

                                Text(
                                    text = "Vehículo: ${record.vehiclePlate}",
                                    color = secondaryText,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Fecha: ${record.date} | ${record.km} km",
                                    color = secondaryText,
                                    fontSize = 13.sp
                                )

                                if (record.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Notas: ${record.notes}",
                                        color = Color.Black,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // 🔥 Eliminación local segura
                                TextButton(
                                    onClick = { userViewModel.maintenanceList.remove(record) }
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = null, tint = MyCarRed)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Eliminar", color = MyCarRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
