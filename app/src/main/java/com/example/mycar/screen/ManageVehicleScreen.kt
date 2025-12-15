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
import androidx.compose.material.icons.filled.Edit
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
import com.example.mycar.network.dto.VehicleResponse
import com.example.mycar.ui.theme.*
import com.example.mycar.components.*

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
    var vehicleToDelete by remember { mutableStateOf<VehicleResponse?>(null) }

    // ID del vehículo que se está editando (null = modo agregar)
    var editingVehicleId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.loadVehicles()
    }

    fun openDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val c = Calendar.getInstance()
                c.set(y, m, d)
                onDateSelected(dateFormat.format(c.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val brandList = listOf(
        "Toyota","Chevrolet","Hyundai","Ford","Kia","Nissan","Volkswagen","Peugeot"
    )

    val modelMap = mapOf(
        "Toyota" to listOf("Yaris","Corolla","Hilux","Rav4"),
        "Chevrolet" to listOf("Spark","Onix","Cruze","Tracker","S10"),
        "Hyundai" to listOf("Accent","Elantra","Tucson","Santa Fe"),
        "Ford" to listOf("Fiesta","Focus","Ranger","Ecosport"),
        "Kia" to listOf("Rio","Cerato","Sportage","Sorento"),
        "Nissan" to listOf("Versa","Sentra","Navara","Kicks"),
        "Volkswagen" to listOf("Gol","Polo","Tiguan","Amarok"),
        "Peugeot" to listOf("208","3008","2008","Partner")
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
                onBack = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(cardColor),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // MARCA
                    var expandedBrand by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedBrand,
                        onExpandedChange = { expandedBrand = !expandedBrand }
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Marca") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedBrand)
                            },
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

                    // MODELO
                    var expandedModel by remember { mutableStateOf(false) }
                    val models = modelMap[brand] ?: emptyList()

                    ExposedDropdownMenuBox(
                        expanded = expandedModel,
                        onExpandedChange = { expandedModel = !expandedModel }
                    ) {
                        OutlinedTextField(
                            value = model,
                            onValueChange = {},
                            readOnly = true,
                            enabled = brand.isNotEmpty(),
                            label = { Text("Modelo") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedModel)
                            },
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

                    // AÑO
                    var expandedYear by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedYear,
                        onExpandedChange = { expandedYear = !expandedYear }
                    ) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Año") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expandedYear)
                            },
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

                    // PLACA
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it },
                        label = { Text("Patente") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = km,
                        onValueChange = { km = it },
                        label = { Text("Kilometraje") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // FECHA SOAP
                    OutlinedTextField(
                        value = soapDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha SOAP") },
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { soapDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // FECHA PERMISO
                    OutlinedTextField(
                        value = permisoDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Permiso circulación") },
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { permisoDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // FECHA REVISIÓN
                    OutlinedTextField(
                        value = revisionDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Revisión Técnica") },
                        trailingIcon = {
                            TextButton(onClick = { openDatePicker { revisionDate = it } }) {
                                Text("Seleccionar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTÓN AGREGAR / EDITAR
                    val buttonText =
                        if (editingVehicleId == null) "Agregar vehículo" else "Guardar cambios"

                    MyCarButton(text = buttonText, icon = Icons.Filled.Add) {

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

                            if (editingVehicleId == null) {
                                // MODO CREAR
                                userViewModel.createVehicle(
                                    brand,
                                    model,
                                    year.toInt(),
                                    plate,
                                    km.toInt(),
                                    soapDate,
                                    permisoDate,
                                    revisionDate
                                ) { success ->
                                    if (success) {
                                        message = "Vehículo agregado correctamente"
                                        isSuccess = true

                                        brand = ""
                                        model = ""
                                        year = ""
                                        plate = ""
                                        km = ""
                                        soapDate = ""
                                        permisoDate = ""
                                        revisionDate = ""
                                    } else {
                                        message = "Error al agregar vehículo"
                                        isSuccess = false
                                    }
                                }
                            } else {
                                // MODO EDITAR
                                userViewModel.updateVehicle(
                                    id = editingVehicleId!!,
                                    brand = brand,
                                    model = model,
                                    year = year.toInt(),
                                    plate = plate,
                                    km = km.toInt(),
                                    soap = soapDate,
                                    permiso = permisoDate,
                                    revision = revisionDate
                                ) { success ->
                                    if (success) {
                                        message = "Vehículo actualizado correctamente"
                                        isSuccess = true

                                        editingVehicleId = null
                                        brand = ""
                                        model = ""
                                        year = ""
                                        plate = ""
                                        km = ""
                                        soapDate = ""
                                        permisoDate = ""
                                        revisionDate = ""
                                    } else {
                                        message = "Error al actualizar vehículo"
                                        isSuccess = false
                                    }
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

            // LISTA DE VEHÍCULOS
            Text("Vehículos registrados:", color = Color.Black)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                items(vehicles.size) { index ->
                    val v = vehicles[index]

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(cardColor),
                        elevation = CardDefaults.cardElevation(4.dp),
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

                            Row {
                                // BOTÓN EDITAR
                                TextButton(onClick = {
                                    editingVehicleId = v.id
                                    brand = v.brand
                                    model = v.model
                                    year = v.year.toString()
                                    plate = v.plate
                                    km = v.km.toString()
                                    soapDate = v.soapDate
                                    permisoDate = v.permisoCirculacionDate
                                    revisionDate = v.revisionTecnicaDate
                                }) {
                                    Icon(Icons.Filled.Edit, null, tint = MyCarBlue)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Editar", color = MyCarBlue)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                TextButton(onClick = {
                                    vehicleToDelete = v
                                    showDeleteDialog = true
                                }) {
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

        if (showDeleteDialog && vehicleToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Seguro que deseas eliminar este vehículo?") },
                confirmButton = {
                    TextButton(onClick = {
                        userViewModel.deleteVehicle(vehicleToDelete!!.id!!) { success ->
                            if (success) {
                                message = "Vehículo eliminado"
                                isSuccess = true
                            } else {
                                message = "Error eliminando vehículo"
                                isSuccess = false
                            }
                        }
                        showDeleteDialog = false
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
