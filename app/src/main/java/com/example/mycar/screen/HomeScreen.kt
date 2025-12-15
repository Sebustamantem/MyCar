package com.example.mycar.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val userName by userViewModel.userName
    val vehicles = userViewModel.vehicles
    val mainVehicle = vehicles.firstOrNull()

    val expenses = userViewModel.expenseApiList
    val maintenances = userViewModel.maintenanceApiList

    // Cargar vehículos al iniciar
    LaunchedEffect(Unit) {
        userViewModel.loadVehicles()
    }

    // ===== Loading bonito para el resumen =====
    var isLoadingSummary by remember { mutableStateOf(true) }

    LaunchedEffect(mainVehicle?.id) {
        isLoadingSummary = true
        val v = mainVehicle ?: return@LaunchedEffect

        userViewModel.loadExpensesByVehicle(v.id)
        userViewModel.loadMaintenanceByVehicle(v.id)

        // Espera hasta que cambie el tamaño de listas (o timeout por si viene vacío)
        withTimeoutOrNull(4500) {
            snapshotFlow { expenses.size to maintenances.size }
                .distinctUntilChanged()
                .first { (e, m) -> e > 0 || m > 0 }
        }

        // Si no llegó nada (porque realmente no hay registros), igual dejamos de cargar
        delay(250)
        isLoadingSummary = false
    }

    // ===== Helpers de fechas =====
    fun parseDate(dateStr: String?): Date? {
        if (dateStr.isNullOrBlank()) return null
        val patterns = listOf("yyyy-MM-dd", "dd/MM/yyyy")
        for (p in patterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.getDefault())
                sdf.isLenient = false
                return sdf.parse(dateStr)
            } catch (_: Exception) { }
        }
        return null
    }

    fun isInThisMonth(d: Date): Boolean {
        val cal = Calendar.getInstance()
        val nowY = cal.get(Calendar.YEAR)
        val nowM = cal.get(Calendar.MONTH)
        cal.time = d
        return cal.get(Calendar.YEAR) == nowY && cal.get(Calendar.MONTH) == nowM
    }

    // ===== Resumen del mes =====
    val monthExpenses = remember(expenses) {
        expenses.filter { e ->
            val d = parseDate(e.date)
            d != null && isInThisMonth(d)
        }
    }

    val monthMaintenances = remember(maintenances) {
        maintenances.filter { m ->
            val d = parseDate(m.date)
            d != null && isInThisMonth(d)
        }
    }

    val totalSpent = remember(monthExpenses) {
        monthExpenses.sumOf { it.amount ?: 0 }
    }

    val maintenanceCount = remember(monthMaintenances) { monthMaintenances.size }

    val clp = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    // ===== UI =====
    val backgroundBrush = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val cardColor = Color.White
    val textColor = Color.Black
    val secondaryText = Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    // ===================== HEADER =====================
                    Text(
                        text = "Hola, ${if (userName.isNotEmpty()) userName else "Usuario"}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tu vehículo principal:", color = secondaryText, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(visible = mainVehicle != null) {
                        Column {
                            Text(
                                text = "${mainVehicle?.brand} ${mainVehicle?.model} (${mainVehicle?.year})",
                                fontWeight = FontWeight.Bold,
                                color = MyCarBlue,
                                fontSize = 18.sp
                            )
                            Text("Patente: ${mainVehicle?.plate}", color = secondaryText)
                            Text("Km actuales: ${mainVehicle?.km} km", color = secondaryText)
                        }
                    }

                    AnimatedVisibility(visible = mainVehicle == null) {
                        Text(
                            text = "Aún no has agregado ningún vehículo.",
                            color = secondaryText,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // ===================== BOTONES  =====================
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ElevatedButton(
                            onClick = { navController.navigate("vehicles") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = "Vehículos", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gestionar", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("maintenanceHistory") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.History, contentDescription = "Historial")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Historial")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("maintenance") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Build, contentDescription = "Mantenimientos")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mantenimientos")
                        }

                        // Gastos
                        OutlinedButton(
                            onClick = { navController.navigate("expenses") },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.ReceiptLong, contentDescription = "Gastos")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gastos")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("profile") },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MyCarBlue),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "Perfil")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Perfil")
                        }
                    }

                    // ===================== RESUMEN =====================
                    Spacer(modifier = Modifier.height(18.dp))

                    MonthlySummaryCard(
                        isLoading = (isLoadingSummary && mainVehicle != null),
                        totalSpentText = clp.format(totalSpent),
                        maintenanceCount = maintenanceCount,
                        onGoExpenseHistory = { navController.navigate("expenseHistory") },
                        onGoMaintenanceHistory = { navController.navigate("maintenanceHistory") }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(
    isLoading: Boolean,
    totalSpentText: String,
    maintenanceCount: Int,
    onGoExpenseHistory: () -> Unit,
    onGoMaintenanceHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Este mes",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                SummarySkeleton()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Gastos", color = Color.DarkGray, fontSize = 12.sp)
                        Text(totalSpentText, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Mantenciones", color = Color.DarkGray, fontSize = 12.sp)
                        Text("$maintenanceCount", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Resumen del mes (gastos y mantenciones)",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onGoExpenseHistory,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hist. Gastos")
                    }

                    OutlinedButton(
                        onClick = onGoMaintenanceHistory,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hist. Mant.")
                    }
                }
            }
        }
    }
}

@Composable
private fun SummarySkeleton() {
    val infinite = rememberInfiniteTransition(label = "shimmer")
    val x by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "x"
    )

    val shimmer = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.25f),
            Color.LightGray.copy(alpha = 0.45f),
            Color.LightGray.copy(alpha = 0.25f)
        ),
        start = Offset(x - 300f, 0f),
        end = Offset(x, 0f)
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.45f)
                        .background(shimmer, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.6f)
                        .background(shimmer, RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.45f)
                        .background(shimmer, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.35f)
                        .background(shimmer, RoundedCornerShape(8.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .height(12.dp)
                .fillMaxWidth(0.7f)
                .background(shimmer, RoundedCornerShape(8.dp))
        )
    }
}
