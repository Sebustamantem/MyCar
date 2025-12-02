package com.example.mycar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycar.UserViewModel
import com.example.mycar.components.*
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarGreen
import com.example.mycar.ui.theme.MyCarLightBlue
import com.example.mycar.ui.theme.MyCarRed

// --------------------------------------------------------------
// Validación simple
// --------------------------------------------------------------
fun validarCampos(
    nombre: String,
    apellido: String,
    correo: String,
    telefono: String,
    contrasena: String,
    confirmar: String
): Pair<Boolean, String> {

    if (nombre.isBlank()) return false to "Ingresa tu nombre."
    if (apellido.isBlank()) return false to "Ingresa tu apellido."

    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    if (!correo.matches(emailRegex)) return false to "Correo inválido."

    val phoneRegex = Regex("^\\+569\\d{8}\$")
    if (!telefono.matches(phoneRegex)) return false to "Teléfono debe ser +569XXXXXXXX."

    if (contrasena.length < 6) return false to "La contraseña debe tener al menos 6 caracteres."
    if (contrasena != confirmar) return false to "Las contraseñas no coinciden."

    return true to ""
}

// --------------------------------------------------------------
// Pantalla de Registro
// --------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit
) {
    val fondo = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))

    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(fondo)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        if (isLoading) {
            MyCarLoading(text = "Creando cuenta...")
            return@Box
        }

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

                Text(
                    text = "Crear cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyCarBlue
                )

                Spacer(modifier = Modifier.height(20.dp))

                MyCarTextField(nombre, { nombre = it }, "Nombre")
                Spacer(modifier = Modifier.height(12.dp))

                MyCarTextField(apellido, { apellido = it }, "Apellido")
                Spacer(modifier = Modifier.height(12.dp))

                MyCarTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo electrónico",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(12.dp))

                MyCarTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "Teléfono (+569XXXXXXXX)",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(modifier = Modifier.height(12.dp))

                MyCarTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = "Contraseña",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                MyCarTextField(
                    value = confirmar,
                    onValueChange = { confirmar = it },
                    label = "Confirmar contraseña",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón registrar
                MyCarButton(text = "Registrarse") {

                    val (ok, msg) = validarCampos(
                        nombre, apellido, correo, telefono,
                        contrasena, confirmar
                    )

                    if (!ok) {
                        mensaje = msg
                        error = true
                        return@MyCarButton
                    }

                    isLoading = true
                    mensaje = ""

                    userViewModel.registerUser(
                        name = nombre,
                        lastName = apellido,
                        email = correo,
                        password = contrasena,
                        phone = telefono
                    ) { success ->
                        isLoading = false
                        if (success) {
                            mensaje = "Cuenta creada exitosamente 🎉"
                            error = false
                            // Navega a login
                            onRegistered()
                        } else {
                            mensaje = "Ese correo ya está registrado."
                            error = true
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MyCarOutlinedButton(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    onClick = onGoLogin
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (mensaje.isNotEmpty()) {
                    MyCarSnackbar(
                        message = mensaje,
                        color = if (error) MyCarRed else MyCarGreen
                    )
                }
            }
        }
    }
}
