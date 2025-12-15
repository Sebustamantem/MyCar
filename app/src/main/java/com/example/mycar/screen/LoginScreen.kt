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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarBlue.copy(alpha = 0.12f), Color.White))

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Validación simple
    val emailTrimmed = email.trim()
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    val emailOk = emailTrimmed.isNotBlank() && emailRegex.matches(emailTrimmed)
    val passOk = password.isNotBlank() && password.length >= 6
    val canLogin = emailOk && passOk && !isLoading

    fun doLogin() {
        if (!emailOk) {
            message = "Ingresa un correo válido."
            isError = true
            return
        }
        if (!passOk) {
            message = "La contraseña debe tener al menos 6 caracteres."
            isError = true
            return
        }

        isLoading = true
        message = ""
        isError = false

        userViewModel.loginUser(emailTrimmed, password) { success ->
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
                Text(
                    text = "Iniciar sesión",
                    fontSize = 24.sp,
                    color = MyCarBlue,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                MyCarTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (message.isNotEmpty()) message = "" // limpia mensajes al escribir
                    },
                    label = "Correo electrónico",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                MyCarTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (message.isNotEmpty()) message = ""
                    },
                    label = "Contraseña",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón igual visualmente, pero bloquea doble click y datos inválidos
                MyCarButton(text = "Ingresar") {
                    if (canLogin) doLogin()
                    else {
                        // Mensaje suave si aprieta sin completar
                        message = "Completa correo y contraseña."
                        isError = true
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MyCarOutlinedButton(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    onClick = {
                        email = ""
                        password = ""
                        message = ""
                        isError = false
                        onGoRegister()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

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
