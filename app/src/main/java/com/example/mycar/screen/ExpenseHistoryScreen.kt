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
import com.example.mycar.network.dto.ExpenseRequest
import com.example.mycar.network.dto.ExpenseResponse
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue
import com.example.mycar.ui.theme.MyCarRed
import kotlinx.coroutines.launch

@Composable
fun ExpenseHistoryScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val list = userViewModel.expenseApiList

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selected by remember { mutableStateOf<ExpenseResponse?>(null) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->

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
                    title = "Historial de Gastos",
                    onBack = { navController.popBackStack() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay gastos registrados.", color = Color.Gray)
                    }
                } else {


                    val sorted = list.sortedByDescending { it.date }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(sorted.size) { i ->
                            val r = sorted[i]

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(Color.White),
                                elevation = CardDefaults.cardElevation(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {

                                    Text(
                                        text = "${r.category} - ${r.type}",
                                        fontWeight = FontWeight.Bold,
                                        color = MyCarBlue
                                    )

                                    Text("Vehículo: ${r.vehiclePlate}", color = Color.Gray)
                                    Text("Fecha: ${r.date}", color = Color.Gray)
                                    Text("Monto: ${formatCLP(r.amount)}", color = Color.Gray)

                                    r.km?.let { Text("KM: $it", color = Color.Gray) }

                                    if (!r.notes.isNullOrBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text("Notas: ${r.notes}", color = Color.DarkGray)
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        TextButton(onClick = { selected = r }) {
                                            Icon(Icons.Filled.Edit, null, tint = MyCarBlue)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Editar", color = MyCarBlue)
                                        }

                                        TextButton(onClick = {
                                            userViewModel.deleteExpenseApi(r.id) { ok ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (ok) "Gasto eliminado" else "Error al eliminar"
                                                    )
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
            }

            selected?.let { e ->
                EditExpenseDialog(
                    expense = e,
                    onDismiss = { selected = null },
                    onSave = { req ->
                        userViewModel.updateExpense(
                            id = e.id,
                            vehicleId = req.vehicleId,
                            vehiclePlate = req.vehiclePlate,
                            category = req.category,
                            type = req.type,
                            date = req.date,
                            amount = req.amount,
                            km = req.km,
                            notes = req.notes
                        ) { ok ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (ok) "Gasto actualizado"
                                    else "Error al actualizar gasto"
                                )
                            }
                            if (ok) {
                                // ✅ Se ve altiro por el update en la lista del ViewModel
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
private fun EditExpenseDialog(
    expense: ExpenseResponse,
    onDismiss: () -> Unit,
    onSave: (ExpenseRequest) -> Unit
) {
    var category by remember { mutableStateOf(expense.category) }
    var type by remember { mutableStateOf(expense.type) }
    var date by remember { mutableStateOf(expense.date) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var km by remember { mutableStateOf(expense.km?.toString() ?: "") }
    var notes by remember { mutableStateOf(expense.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar gasto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría") },
                    singleLine = true
                )

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
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = km,
                    onValueChange = { km = it },
                    label = { Text("KM (opcional)") },
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
                val amountInt = amount.toIntOrNull() ?: expense.amount
                val kmInt = km.toIntOrNull()

                onSave(
                    ExpenseRequest(
                        vehicleId = expense.vehicleId,
                        vehiclePlate = expense.vehiclePlate,
                        category = category,
                        type = type,
                        date = date,
                        amount = amountInt,
                        km = kmInt,
                        notes = notes.ifBlank { null }
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

private fun formatCLP(value: Int): String {
    val s = value.toString()
    val reversed = s.reversed()
    val chunks = reversed.chunked(3).joinToString(".")
    return "$" + chunks.reversed()
}
