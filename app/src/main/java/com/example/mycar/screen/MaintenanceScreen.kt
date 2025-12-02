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
import com.example.mycar.UserViewModel
import com.example.mycar.models.VehicleData
import com.example.mycar.models.MaintenanceRecord
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
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White
    val textColor = Color.Black

    val maintenanceCategories = mapOf(
        "Motor" to listOf("Cambio de aceite", "Cambio de bujías", "Cambio de filtro de aire"),
        "Suspensión" to listOf("Cambio de amortiguadores", "Alineación y balanceo"),
        "Frenos" to listOf("Cambio de pastillas", "Cambio de líquido de frenos"),
        "Transmisión" to listOf("Cambio de aceite de caja", "Revisión de embrague")
    )

    val vehicles = userViewModel.vehicles
    val maintenanceList = userViewModel.maintenanceList

    var selectedVehicle by remember { mutableStateOf<VehicleData?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var km by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

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

            ScreenHeader(
                title = "Mantenimientos",
                onBack = {
                    val popped = navController.popBackStack("home", false)
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // VEHÍCULO
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
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedVehicle)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedVehicle,
                            onDismissRequest = { expandedVehicle = false }
                        ) {
                            vehicles.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text("${v.brand} ${v.model} (${v.plate})") },
                                    onClick = {
                                        selectedVehicle = v
                                        km = v.km
                                        expandedVehicle = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedVehicle != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MyCarBlue.copy(0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Vehículo: ${selectedVehicle!!.brand} ${selectedVehicle!!.model}",
                                    color = MyCarBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Patente: ${selectedVehicle!!.plate}", color = Color.Gray)
                                Text("KM actual: ${selectedVehicle!!.km} km", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CATEGORÍA
                    var expandedCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            maintenanceCategories.keys.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        selectedCategory = c
                                        selectedType = ""
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // TIPO
                    var expandedType by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType }
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedType)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            val types = maintenanceCategories[selectedCategory] ?: emptyList()
                            types.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = {
                                        selectedType = t
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // KM
                    MyCarTextField(
                        value = km,
                        onValueChange = { km = it },
                        label = "Kilometraje actual",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // NOTAS
                    MyCarTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Notas (opcional)"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // AGREGAR
                    MyCarButton(text = "Agregar mantenimiento", icon = Icons.Filled.Add) {

                        if (selectedVehicle != null &&
                            selectedCategory.isNotBlank() &&
                            selectedType.isNotBlank()
                        ) {
                            val record = MaintenanceRecord(
                                vehiclePlate = selectedVehicle!!.plate,
                                type = "$selectedCategory - $selectedType",
                                date = currentDate,
                                km = km,
                                notes = notes
                            )

                            userViewModel.addMaintenance(record)

                            // ACTUALIZAR KM
                            val index = userViewModel.vehicles.indexOfFirst {
                                it.plate == selectedVehicle!!.plate
                            }
                            if (index != -1) {
                                val updated = selectedVehicle!!.copy(km = km)
                                userViewModel.vehicles[index] = updated
                                selectedVehicle = updated
                            }

                            selectedCategory = ""
                            selectedType = ""
                            km = ""
                            notes = ""
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ElevatedButton(
                        onClick = { navController.navigate("maintenanceHistory") },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(MyCarBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.History, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver historial", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // LISTA DE MANTENIMIENTOS
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(maintenanceList.size) { index ->
                    val record = maintenanceList[index]

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(cardColor),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(record.type, fontWeight = FontWeight.Bold)
                            Text("Vehículo: ${record.vehiclePlate}", color = Color.Gray)
                            Text("Fecha: ${record.date} | ${record.km} km", color = Color.Gray)
                            if (record.notes.isNotBlank())
                                Text("Notas: ${record.notes}", color = Color.DarkGray)

                            Spacer(modifier = Modifier.height(6.dp))

                            TextButton(
                                onClick = {
                                    userViewModel.removeMaintenance(record)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Mantenimiento eliminado",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Delete, null, tint = MyCarRed)
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
