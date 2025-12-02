package com.example.mycar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycar.UserViewModel
import com.example.mycar.components.*
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarGreen
import com.example.mycar.ui.theme.MyCarRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {
    // Fondo degradado
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarBlue.copy(alpha = 0.12f), Color.White))

    // Estados UI
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Limpiar estado al entrar (evita mensajes/residuos)
    LaunchedEffect(Unit) {
        email = ""
        password = ""
        message = ""
        isError = false
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            MyCarLoading(text = "Iniciando sesión...")
            return@Box
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = "Iniciar sesión",
                    fontSize = 24.sp,
                    color = MyCarBlue,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo email
                MyCarTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo contraseña
                MyCarTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón ingresar
                MyCarButton(text = "Ingresar") {
                    // Validación rápida
                    if (email.isBlank() || password.isBlank()) {
                        message = "Por favor completa todos los campos."
                        isError = true
                        return@MyCarButton
                    }

                    // Mostrar loader y llamar ViewModel
                    isLoading = true
                    message = ""
                    isError = false

                    coroutineScope.launch {
                        // Pequeña pausa para UX consistente
                        delay(300)

                        // Llamada al ViewModel (usa callback boolean)
                        userViewModel.loginUser(email, password) { success ->
                            // Ejecuta en hilo de UI porque callback se llama en ViewModelScope
                            isLoading = false
                            if (success) {
                                message = "Inicio de sesión exitoso"
                                isError = false
                                onLoginSuccess()
                            } else {
                                message = "Correo o contraseña incorrectos."
                                isError = true
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón registro
                MyCarOutlinedButton(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    onClick = {
                        // limpiar estados antes de navegar
                        email = ""
                        password = ""
                        message = ""
                        isError = false
                        onGoRegister()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Snackbar del mensaje (usamos componente custom)
                if (message.isNotEmpty()) {
                    MyCarSnackbar(
                        message = message,
                        color = if (isError) MyCarRed else MyCarGreen
                    )
                }
            }
        }
    }
}
