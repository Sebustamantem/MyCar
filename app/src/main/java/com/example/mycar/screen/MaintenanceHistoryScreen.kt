package com.example.mycar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.mycar.components.ScreenHeader
import com.example.mycar.network.dto.MaintenanceRequest
import com.example.mycar.network.dto.MaintenanceResponse
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue
import com.example.mycar.ui.theme.MyCarRed
import kotlinx.coroutines.launch

@Composable
fun MaintenanceHistoryScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White

    val maintenanceList: List<MaintenanceResponse> = userViewModel.maintenanceApiList

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selected by remember { mutableStateOf<MaintenanceResponse?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                ScreenHeader(
                    title = "Historial de Mantenimientos",
                    onBack = { navController.popBackStack() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (maintenanceList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay mantenimientos registrados.", color = Color.Gray)
                    }
                } else {


                    val sortedList = maintenanceList.sortedByDescending { it.date }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sortedList.size) { index ->
                            val record = sortedList[index]

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(cardColor),
                                elevation = CardDefaults.cardElevation(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {

                                    Text(
                                        text = record.type,
                                        fontWeight = FontWeight.Bold,
                                        color = MyCarBlue
                                    )

                                    Text(
                                        text = "Vehículo: ${record.vehiclePlate}",
                                        color = Color.Gray
                                    )

                                    Text(
                                        text = "Fecha: ${record.date} | ${record.km} km",
                                        color = Color.Gray
                                    )

                                    Text(
                                        text = "Costo: $${record.cost}",
                                        color = Color.Gray
                                    )

                                    if (!record.notes.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Notas: ${record.notes}",
                                            color = Color.DarkGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        TextButton(onClick = { selected = record }) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = null,
                                                tint = MyCarBlue
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Editar", color = MyCarBlue)
                                        }

                                        TextButton(
                                            onClick = {
                                                userViewModel.deleteMaintenanceApi(record.id) { success ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            if (success) "Mantenimiento eliminado"
                                                            else "Error al eliminar mantenimiento"
                                                        )
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = null,
                                                tint = MyCarRed
                                            )
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

            selected?.let { m ->
                EditMaintenanceDialog(
                    maintenance = m,
                    onDismiss = { selected = null },
                    onSave = { req ->
                        userViewModel.updateMaintenance(
                            id = m.id,
                            vehicleId = req.vehicleId,
                            vehiclePlate = req.vehiclePlate,
                            type = req.type,
                            date = req.date,
                            km = req.km,
                            notes = req.notes,
                            cost = req.cost
                        ) { ok ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (ok) "Mantenimiento actualizado"
                                    else "Error al actualizar mantenimiento"
                                )
                            }
                            if (ok) {
                                // Cerrar dialog, y la lista se actualiza sola por el ViewModel
                                selected = null
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EditMaintenanceDialog(
    maintenance: MaintenanceResponse,
    onDismiss: () -> Unit,
    onSave: (MaintenanceRequest) -> Unit
) {
    var type by remember { mutableStateOf(maintenance.type) }
    var date by remember { mutableStateOf(maintenance.date) }
    var km by remember { mutableStateOf(maintenance.km.toString()) }
    var notes by remember { mutableStateOf(maintenance.notes ?: "") }
    var cost by remember { mutableStateOf(maintenance.cost.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar mantenimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Tipo") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (dd/MM/yyyy)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = km,
                    onValueChange = { km = it },
                    label = { Text("Km") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Costo") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val kmInt = km.toIntOrNull() ?: maintenance.km
                val costInt = cost.toIntOrNull() ?: maintenance.cost

                onSave(
                    MaintenanceRequest(
                        vehicleId = maintenance.vehicleId,
                        vehiclePlate = maintenance.vehiclePlate,
                        type = type,
                        date = date,
                        km = kmInt,
                        notes = notes.ifBlank { null },
                        cost = costInt
                    )
                )
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
