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
import com.example.mycar.model.VehicleData
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

    // Estados del formulario
    var brand by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var km by rememberSaveable { mutableStateOf("") }

    var soapDate by rememberSaveable { mutableStateOf("") }
    var permisoDate by rememberSaveable { mutableStateOf("") }
    var revisionDate by rememberSaveable { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var vehicleToDelete by remember { mutableStateOf<VehicleData?>(null) }

    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { userViewModel.loadVehicles() }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    fun openDatePicker(onSelected: (String) -> Unit) {
        val dialog = DatePickerDialog(
            context,
            { _, y, m, d ->
                val selected = Calendar.getInstance()
                selected.set(y, m, d)
                onSelected(dateFormat.format(selected.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    // LISTAS
    val brandList = listOf("Toyota", "Chevrolet", "Hyundai", "Ford", "Kia", "Nissan", "Volkswagen", "Peugeot")
    val modelMap = mapOf(
        "Toyota" to listOf("Yaris", "Corolla", "Hilux", "Rav4"),
        "Chevrolet" to listOf("Spark", "Onix", "Cruze", "Tracker"),
        "Hyundai" to listOf("Accent", "Tucson", "Elantra", "Santa Fe"),
        "Ford" to listOf("Fiesta", "Focus", "Ranger", "Ecosport"),
        "Kia" to listOf("Rio", "Cerato", "Sportage", "Sorento"),
        "Nissan" to listOf("Versa", "Sentra", "Kicks", "Navara"),
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
                    var expBrand by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expBrand,
                        onExpandedChange = { expBrand = !expBrand }
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Marca") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expBrand) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expBrand,
                            onDismissRequest = { expBrand = false }
                        ) {
                            brandList.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b) },
                                    onClick = {
                                        brand = b
                                        model = ""
                                        expBrand = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Modelo
                    var expModel by remember { mutableStateOf(false) }
                    val models = modelMap[brand] ?: emptyList()

                    ExposedDropdownMenuBox(
                        expanded = expModel,
                        onExpandedChange = { expModel = !expModel }
                    ) {
                        OutlinedTextField(
                            value = model,
                            onValueChange = {},
                            readOnly = true,
                            enabled = brand.isNotEmpty(),
                            label = { Text("Modelo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expModel) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expModel,
                            onDismissRequest = { expModel = false }
                        ) {
                            models.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m) },
                                    onClick = {
                                        model = m
                                        expModel = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Año
                    var expYear by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expYear,
                        onExpandedChange = { expYear = !expYear }
                    ) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Año") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expYear) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expYear,
                            onDismissRequest = { expYear = false }
                        ) {
                            yearList.forEach { y ->
                                DropdownMenuItem(
                                    text = { Text(y.toString()) },
                                    onClick = {
                                        year = y.toString()
                                        expYear = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MyCarTextField(plate, { plate = it }, "Patente")
                    Spacer(modifier = Modifier.height(8.dp))
                    MyCarTextField(km, { km = it }, "Kilometraje")

                    Spacer(modifier = Modifier.height(12.dp))

                    // SOAP
                    OutlinedTextField(
                        value = soapDate,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { soapDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        label = { Text("SOAP") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = permisoDate,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { permisoDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        label = { Text("Permiso de Circulación") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = revisionDate,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { revisionDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        label = { Text("Revisión Técnica") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MyCarButton(text = "Agregar vehículo", icon = Icons.Filled.Add) {

                        if (
                            brand.isNotBlank() &&
                            model.isNotBlank() &&
                            year.isNotBlank() &&
                            plate.isNotBlank() &&
                            km.isNotBlank() &&
                            soapDate.isNotBlank() &&
                            permisoDate.isNotBlank() &&
                            revisionDate.isNotBlank()
                        ) {

                            val newVehicle = VehicleData(
                                brand = brand,
                                model = model,
                                year = year.toInt(),
                                plate = plate,
                                km = km.toIntOrNull() ?: 0,
                                soapDate = soapDate,
                                permisoCirculacionDate = permisoDate,
                                revisionTecnicaDate = revisionDate
                            )

                            userViewModel.addVehicle(newVehicle) { ok ->
                                if (ok) {
                                    message = "Vehículo agregado correctamente"
                                    isSuccess = true
                                    brand = ""; model = ""; year = ""
                                    plate = ""; km = ""
                                    soapDate = ""; permisoDate = ""; revisionDate = ""
                                } else {
                                    message = "Error al agregar vehículo"
                                    isSuccess = false
                                }
                            }
                        } else {
                            message = "Completa todos los campos"
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

        // DIÁLOGO CONFIRMACIÓN
        if (showDeleteDialog && vehicleToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Seguro que deseas eliminar este vehículo?") },
                confirmButton = {
                    TextButton(onClick = {
                        userViewModel.removeVehicle(vehicleToDelete!!)
                        showDeleteDialog = false
                        message = "Vehículo eliminado"
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
