package com.example.mycar.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.VehicleData
import com.example.mycar.components.*
import com.example.mycar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageVehicleScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val vehicles = userViewModel.vehicles
    val context = LocalContext.current

    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White

    // Guardan el estado al rotar
    var brand by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var km by rememberSaveable { mutableStateOf("") }

    var soapDate by rememberSaveable { mutableStateOf("") }
    var permisoDate by rememberSaveable { mutableStateOf("") }
    var revisionDate by rememberSaveable { mutableStateOf("") }

    var message by rememberSaveable { mutableStateOf("") }
    var isSuccess by rememberSaveable { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var vehicleToDelete by remember { mutableStateOf<VehicleData?>(null) }

    LaunchedEffect(Unit) { userViewModel.loadVehicles() }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    fun openDatePicker(onDateSelected: (String) -> Unit) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day)
                onDateSelected(dateFormat.format(selected.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    // Listas para selección
    val brandList = listOf(
        "Toyota", "Chevrolet", "Hyundai", "Ford", "Kia", "Nissan", "Volkswagen", "Peugeot"
    )
    val modelMap = mapOf(
        "Toyota" to listOf("Yaris", "Corolla", "Hilux", "Rav4"),
        "Chevrolet" to listOf("Spark", "Onix", "Cruze", "Tracker", "S10"),
        "Hyundai" to listOf("Accent", "Elantra", "Tucson", "Santa Fe"),
        "Ford" to listOf("Fiesta", "Focus", "Ranger", "Ecosport"),
        "Kia" to listOf("Rio", "Cerato", "Sportage", "Sorento"),
        "Nissan" to listOf("Versa", "Sentra", "Navara", "Kicks"),
        "Volkswagen" to listOf("Gol", "Polo", "Tiguan", "Amarok"),
        "Peugeot" to listOf("208", "3008", "2008", "Partner")
    )
    val yearList = (2000..2025).toList().reversed()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            ScreenHeader(
                title = "Gestión de Vehículos",
                onBack = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Marca
                    var expandedBrand by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedBrand,
                        onExpandedChange = { expandedBrand = !expandedBrand }
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = {},
                            label = { Text("Marca") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrand) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBrand,
                            onDismissRequest = { expandedBrand = false }
                        ) {
                            brandList.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        brand = it
                                        model = ""
                                        expandedBrand = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Modelo
                    var expandedModel by remember { mutableStateOf(false) }
                    val models = modelMap[brand] ?: emptyList()
                    ExposedDropdownMenuBox(
                        expanded = expandedModel,
                        onExpandedChange = { expandedModel = !expandedModel }
                    ) {
                        OutlinedTextField(
                            value = model,
                            onValueChange = {},
                            label = { Text("Modelo") },
                            readOnly = true,
                            enabled = brand.isNotEmpty(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModel) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedModel,
                            onDismissRequest = { expandedModel = false }
                        ) {
                            models.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        model = it
                                        expandedModel = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Año
                    var expandedYear by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedYear,
                        onExpandedChange = { expandedYear = !expandedYear }
                    ) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = {},
                            label = { Text("Año") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedYear,
                            onDismissRequest = { expandedYear = false }
                        ) {
                            yearList.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.toString()) },
                                    onClick = {
                                        year = it.toString()
                                        expandedYear = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MyCarTextField(value = plate, onValueChange = { plate = it }, label = "Patente")
                    Spacer(modifier = Modifier.height(8.dp))
                    MyCarTextField(value = km, onValueChange = { km = it }, label = "Kilometraje")

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fechas
                    OutlinedTextField(
                        value = soapDate,
                        onValueChange = {},
                        label = { Text("Fecha SOAP") },
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { soapDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = permisoDate,
                        onValueChange = {},
                        label = { Text("Permiso de Circulación") },
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { permisoDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = revisionDate,
                        onValueChange = {},
                        label = { Text("Revisión Técnica") },
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { revisionDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Agregar vehículo
                    MyCarButton(text = "Agregar vehículo", icon = Icons.Filled.Add) {
                        if (brand.isNotBlank() && model.isNotBlank() && year.isNotBlank() &&
                            plate.isNotBlank() && km.isNotBlank() &&
                            soapDate.isNotBlank() && permisoDate.isNotBlank() && revisionDate.isNotBlank()
                        ) {
                            val newVehicle = VehicleData(
                                brand = brand,
                                model = model,
                                year = year.toIntOrNull() ?: 0,
                                plate = plate,
                                km = km,
                                soapDate = soapDate,
                                permisoCirculacionDate = permisoDate,
                                revisionTecnicaDate = revisionDate
                            )

                            userViewModel.addVehicle(newVehicle) {
                                // Alertas automáticas
                                userViewModel.addAlert(
                                    "SOAP próximo a vencer",
                                    "El SOAP de ${brand} vence el ${soapDate}."
                                )
                                userViewModel.addAlert(
                                    "Permiso de Circulación",
                                    "El permiso de ${brand} vence el ${permisoDate}."
                                )
                                userViewModel.addAlert(
                                    "Revisión Técnica",
                                    "La revisión técnica de ${brand} vence el ${revisionDate}."
                                )

                                message = "Vehículo agregado correctamente"
                                isSuccess = true
                                brand = ""; model = ""; year = ""
                                plate = ""; km = ""
                                soapDate = ""; permisoDate = ""; revisionDate = ""
                            }
                        } else {
                            message = "Por favor completa todos los campos."
                            isSuccess = false
                        }
                    }

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        MyCarSnackbar(
                            message = message,
                            color = if (isSuccess) MyCarGreen else MyCarRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Vehículos registrados:", color = Color.Black)

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            ) {
                items(vehicles.size) { index ->
                    val v = vehicles[index]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${v.brand} ${v.model} (${v.year})", color = MyCarBlue)
                            Text("Patente: ${v.plate} | ${v.km} km")
                            Text("SOAP: ${v.soapDate}")
                            Text("Permiso: ${v.permisoCirculacionDate}")
                            Text("Revisión: ${v.revisionTecnicaDate}")

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = {
                                vehicleToDelete = v
                                showDeleteDialog = true
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MyCarRed)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Eliminar", color = MyCarRed)
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog && vehicleToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Seguro que deseas eliminar este vehículo?") },
                confirmButton = {
                    TextButton(onClick = {
                        userViewModel.removeVehicle(vehicleToDelete!!)
                        showDeleteDialog = false
                        message = "Vehículo eliminado correctamente"
                        isSuccess = true
                    }) {
                        Text("Eliminar", color = MyCarRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = MyCarBlue)
                    }
                }
            )
        }
    }
}