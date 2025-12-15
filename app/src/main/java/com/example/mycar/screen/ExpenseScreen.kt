package com.example.mycar.screen

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.components.MyCarButton
import com.example.mycar.components.MyCarTextField
import com.example.mycar.components.ScreenHeader
import com.example.mycar.network.dto.VehicleResponse
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue
import com.example.mycar.ui.theme.MyCarRed
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White

    val categories = mapOf(
        "Combustible" to listOf("Bencina 93", "Bencina 95", "Bencina 97", "Diesel", "Carga eléctrica"),
        "Peajes" to listOf("TAG", "Peaje manual"),
        "Estacionamiento" to listOf("Parking", "Parquímetro"),
        "Lavado" to listOf("Lavado simple", "Lavado full", "Aspirado"),
        "Seguro" to listOf("SOAP", "Seguro anual", "Seguro mensual"),
        "Permisos" to listOf("Permiso circulación", "Revisión técnica", "Multa/Parte"),
        "Otros" to listOf("Accesorios", "Repuestos", "Servicio")
    )

    val vehicles = userViewModel.vehicles
    val expenseList = userViewModel.expenseApiList

    var selectedVehicle by remember { mutableStateOf<VehicleResponse?>(null) }

    var selectedCategory by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }

    var amount by remember { mutableStateOf("") }
    var km by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var selectedDate by remember { mutableStateOf(dateFormat.format(Date())) }

    var editingExpenseId by remember { mutableStateOf<Long?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { userViewModel.loadVehicles() }

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        try { dateFormat.parse(selectedDate)?.let { cal.time = it } } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val c = Calendar.getInstance()
                c.set(y, m, d)
                selectedDate = dateFormat.format(c.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun resetForm() {
        selectedCategory = ""
        selectedType = ""
        amount = ""
        km = ""
        notes = ""
        selectedDate = dateFormat.format(Date())
        editingExpenseId = null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ==========================
            // HEADER
            // ==========================
            item {
                ScreenHeader(
                    title = "Gastos",
                    onBack = {
                        val popped = navController.popBackStack("home", false)
                        if (!popped) navController.navigate("home")
                    }
                )
            }

            // ==========================
            // FORM CARD
            // ==========================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        if (editingExpenseId != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AssistChip(onClick = {}, label = { Text("Editando") })
                                TextButton(onClick = { resetForm() }) {
                                    Text("Cancelar", color = MyCarBlue)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // VEHÍCULO
                        var expandedVehicle by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedVehicle,
                            onExpandedChange = { expandedVehicle = !expandedVehicle }
                        ) {
                            OutlinedTextField(
                                value = selectedVehicle?.let { "${it.brand} ${it.model} (${it.plate})" } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Seleccionar vehículo") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedVehicle) },
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
                                            expandedVehicle = false
                                            editingExpenseId = null
                                            userViewModel.loadExpensesByVehicle(v.id)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // FECHA
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha") },
                            trailingIcon = {
                                IconButton(onClick = { openDatePicker() }) {
                                    Icon(Icons.Filled.Event, contentDescription = null, tint = MyCarBlue)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

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
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false }
                            ) {
                                categories.keys.forEach { c ->
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
                        val types = categories[selectedCategory] ?: emptyList()

                        ExposedDropdownMenuBox(
                            expanded = expandedType,
                            onExpandedChange = { expandedType = !expandedType }
                        ) {
                            OutlinedTextField(
                                value = selectedType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo") },
                                enabled = selectedCategory.isNotBlank(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedType) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = { expandedType = false }
                            ) {
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

                        // MONTO (CLP)
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it.filter(Char::isDigit) },
                            label = { Text("Monto (CLP)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            supportingText = {
                                Text("Vista previa: ${formatCLP(amount.toIntOrNull() ?: 0)}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // KM (opcional)
                        MyCarTextField(
                            value = km,
                            onValueChange = { km = it.filter(Char::isDigit) },
                            label = "Kilometraje (opcional)",
                            keyboardType = KeyboardType.Number
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // NOTAS
                        MyCarTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "Notas (opcional)"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // BOTÓN AGREGAR / GUARDAR  ✅ (ya no se corta)
                        MyCarButton(
                            text = if (editingExpenseId == null) "Agregar gasto" else "Guardar cambios",
                            icon = if (editingExpenseId == null) Icons.Filled.Add else Icons.Filled.Edit
                        ) {
                            val v = selectedVehicle
                            val amountInt = amount.toIntOrNull()
                            val kmInt = km.toIntOrNull()

                            if (v == null) {
                                scope.launch { snackbarHostState.showSnackbar("Selecciona un vehículo") }
                                return@MyCarButton
                            }
                            if (selectedCategory.isBlank() || selectedType.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Selecciona categoría y tipo") }
                                return@MyCarButton
                            }
                            if (amountInt == null || amountInt <= 0) {
                                scope.launch { snackbarHostState.showSnackbar("Ingresa un monto válido") }
                                return@MyCarButton
                            }

                            if (editingExpenseId == null) {
                                userViewModel.createExpense(
                                    vehicleId = v.id,
                                    vehiclePlate = v.plate,
                                    category = selectedCategory,
                                    type = selectedType,
                                    date = selectedDate,
                                    amount = amountInt,
                                    km = kmInt,
                                    notes = notes.ifBlank { null }
                                ) { success ->
                                    scope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar("Gasto agregado")
                                            userViewModel.loadExpensesByVehicle(v.id)
                                            resetForm()
                                        } else {
                                            snackbarHostState.showSnackbar("Error al agregar gasto")
                                        }
                                    }
                                }
                            } else {
                                userViewModel.updateExpense(
                                    id = editingExpenseId!!,
                                    vehicleId = v.id,
                                    vehiclePlate = v.plate,
                                    category = selectedCategory,
                                    type = selectedType,
                                    date = selectedDate,
                                    amount = amountInt,
                                    km = kmInt,
                                    notes = notes.ifBlank { null }
                                ) { success ->
                                    scope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar("Gasto actualizado")
                                            userViewModel.loadExpensesByVehicle(v.id)
                                            resetForm()
                                        } else {
                                            snackbarHostState.showSnackbar("Error al actualizar gasto")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // BOTÓN HISTORIAL ✅
                        ElevatedButton(
                            onClick = { navController.navigate("expenseHistory") },
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
            }

            // ==========================
            // TÍTULO LISTA
            // ==========================
            item {
                Text(
                    text = "Gastos del vehículo:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ==========================
            // ESTADOS / LISTA
            // ==========================
            when {
                selectedVehicle == null -> {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Selecciona un vehículo.", color = Color.Gray)
                        }
                    }
                }

                expenseList.isEmpty() -> {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No hay gastos para este vehículo.", color = Color.Gray)
                        }
                    }
                }

                else -> {
                    items(expenseList.size) { index ->
                        val r = expenseList[index]

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(cardColor),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${r.category} - ${r.type}", fontWeight = FontWeight.Bold, color = MyCarBlue)
                                    AssistChip(onClick = {}, label = { Text(formatCLP(r.amount)) })
                                }

                                Spacer(Modifier.height(6.dp))
                                Text("Fecha: ${r.date}", color = Color.Gray)
                                if (r.km != null) Text("KM: ${r.km}", color = Color.Gray)

                                if (!r.notes.isNullOrBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text("Notas: ${r.notes}", color = Color.DarkGray)
                                }

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = {
                                        editingExpenseId = r.id
                                        selectedCategory = r.category
                                        selectedType = r.type
                                        selectedDate = r.date
                                        amount = r.amount.toString()
                                        km = r.km?.toString() ?: ""
                                        notes = r.notes ?: ""
                                        scope.launch { snackbarHostState.showSnackbar("Editando...") }
                                    }) {
                                        Icon(Icons.Filled.Edit, null, tint = MyCarBlue)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Editar", color = MyCarBlue)
                                    }

                                    Spacer(Modifier.width(6.dp))

                                    TextButton(onClick = {
                                        userViewModel.deleteExpenseApi(r.id) { ok ->
                                            scope.launch {
                                                if (ok) {
                                                    snackbarHostState.showSnackbar("Eliminado")
                                                    if (editingExpenseId == r.id) resetForm()
                                                    selectedVehicle?.let { userViewModel.loadExpensesByVehicle(it.id) }
                                                } else {
                                                    snackbarHostState.showSnackbar("Error al eliminar")
                                                }
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.Delete, null, tint = MyCarRed)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Eliminar", color = MyCarRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // espacio final para que no lo tape la bottom bar
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

// Formato CLP: 300000 -> $300.000
private fun formatCLP(value: Int): String {
    val s = value.toString()
    val reversed = s.reversed()
    val chunks = reversed.chunked(3).joinToString(".")
    return "$" + chunks.reversed()
}
