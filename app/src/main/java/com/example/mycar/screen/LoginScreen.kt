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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycar.UserViewModel
import com.example.mycar.components.*
import com.example.mycar.ui.theme.*
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
    val backgroundGradient = Brush.verticalGradient(
        listOf(MyCarLightBlue, Color.White)
    )

    // Estados
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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
        } else {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //  Título
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 24.sp,
                        color = MyCarBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email
                    MyCarTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Correo electrónico",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contraseña
                    MyCarTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de ingresar
                    MyCarButton(text = "Ingresar") {
                        if (email.isBlank() || password.isBlank()) {
                            message = "Por favor completa todos los campos."
                            isError = true
                            isSuccess = false
                            return@MyCarButton
                        }

                        isLoading = true
                        message = ""
                        isError = false
                        isSuccess = false

                        coroutineScope.launch {
                            delay(800) // pequeña animación de carga

                            userViewModel.loginUser(email, password) { success ->
                                isLoading = false

                                if (success) {
                                    message = "Inicio de sesión exitoso 🎉"
                                    isError = false
                                    isSuccess = true
                                    onLoginSuccess()
                                } else {
                                    message = "Correo o contraseña incorrectos."
                                    isError = true
                                    isSuccess = false
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón de registro
                    MyCarOutlinedButton(
                        text = "¿No tienes cuenta? Regístrate aquí",
                        onClick = onGoRegister
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    //  Mensaje
                    if (message.isNotEmpty()) {
                        MyCarSnackbar(
                            message = message,
                            color = when {
                                isSuccess -> MyCarGreen
                                isError -> MyCarRed
                                else -> MyCarBlue
                            }
                        )
                    }
                }
            }
        }
    }
}
