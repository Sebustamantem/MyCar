package com.example.mycar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycar.MaintenanceRecord
import com.example.mycar.UserViewModel
import com.example.mycar.VehicleData
import com.example.mycar.components.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.mycar.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    //  Fondo degradado
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White
    val textColor = Color.Black

    // Categorías de mantenimiento
    val maintenanceCategories = mapOf(
        "Motor" to listOf("Cambio de aceite", "Cambio de bujías", "Cambio de filtro de aire"),
        "Suspensión" to listOf("Cambio de amortiguadores", "Alineación y balanceo"),
        "Frenos" to listOf("Cambio de pastillas", "Cambio de líquido de frenos"),
        "Transmisión" to listOf("Cambio de aceite de caja", "Revisión de embrague")
    )

    val vehicles = userViewModel.vehicles
    val maintenanceList = userViewModel.maintenanceList

    // Campos del formulario
    var selectedVehicle by remember { mutableStateOf<VehicleData?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var km by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    // Estado para snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            ScreenHeader(
                title = "Mantenimientos",
                onBack = {
                    val popped = navController.popBackStack("home", inclusive = false)
                    if (!popped) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //  Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Seleccionar vehículo
                    var expandedVehicle by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedVehicle,
                        onExpandedChange = { expandedVehicle = !expandedVehicle }
                    ) {
                        OutlinedTextField(
                            value = selectedVehicle?.let { "${it.brand} ${it.model} (${it.plate})" } ?: "",
                            onValueChange = {},
                            label = { Text("Seleccionar vehículo") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehicle)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedVehicle,
                            onDismissRequest = { expandedVehicle = false }
                        ) {
                            vehicles.forEach { vehicle ->
                                DropdownMenuItem(
                                    text = { Text("${vehicle.brand} ${vehicle.model} (${vehicle.plate})") },
                                    onClick = {
                                        selectedVehicle = vehicle
                                        km = vehicle.km
                                        expandedVehicle = false
                                    }
                                )
                            }
                        }
                    }

                    // Info vehículo
                    if (selectedVehicle != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MyCarBlue.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Vehículo: ${selectedVehicle!!.brand} ${selectedVehicle!!.model}",
                                    color = MyCarBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Patente: ${selectedVehicle!!.plate}", color = Color.Gray)
                                Text("Kilometraje actual: ${selectedVehicle!!.km} km", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Categoría
                    var expandedCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            label = { Text("Categoría de mantenimiento") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            maintenanceCategories.keys.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        selectedType = ""
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tipo
                    var expandedType by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType }
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            label = { Text("Tipo de mantenimiento") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            val types = maintenanceCategories[selectedCategory] ?: emptyList()
                            types.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedType = type
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Kilometraje
                    MyCarTextField(
                        value = km,
                        onValueChange = { km = it },
                        label = "Kilometraje actual",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Notas
                    MyCarTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Notas (opcional)"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Agregar mantenimiento
                    MyCarButton(text = "Agregar mantenimiento", icon = Icons.Filled.Add) {
                        if (selectedVehicle != null && selectedCategory.isNotBlank() && selectedType.isNotBlank()) {
                            val record = MaintenanceRecord(
                                vehiclePlate = selectedVehicle!!.plate,
                                type = "$selectedCategory - $selectedType",
                                date = currentDate,
                                km = km,
                                notes = notes
                            )
                            userViewModel.addMaintenance(record)

                            // Actualiza km del vehículo
                            val index = userViewModel.vehicles.indexOfFirst { it.plate == selectedVehicle!!.plate }
                            if (index != -1) {
                                val updatedVehicle = selectedVehicle!!.copy(km = km)
                                userViewModel.vehicles[index] = updatedVehicle
                                selectedVehicle = updatedVehicle
                            }

                            selectedCategory = ""
                            selectedType = ""
                            notes = ""
                            km = ""
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    //  Ver historial
                    ElevatedButton(
                        onClick = { navController.navigate("maintenanceHistory") },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MyCarBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.History, contentDescription = "Historial", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver historial", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  Lista de mantenimientos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(maintenanceList.size) { index ->
                    val record = maintenanceList[index]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(record.type, fontWeight = FontWeight.Bold, color = textColor)
                            Text("Vehículo: ${record.vehiclePlate}", color = Color.Gray)
                            Text("Fecha: ${record.date} | ${record.km} km", color = Color.Gray)
                            if (record.notes.isNotBlank())
                                Text("Notas: ${record.notes}", color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(onClick = {
                                userViewModel.removeMaintenance(record)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Mantenimiento eliminado",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }) {
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
