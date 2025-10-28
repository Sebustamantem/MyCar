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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycar.UserViewModel
import com.example.mycar.components.*
import com.example.mycar.ui.theme.*
import androidx.compose.runtime.saveable.rememberSaveable


// --------------------------------------------------------------
// Validación de campos
// --------------------------------------------------------------
private fun validarCampos(
    nombre: String, apellido: String, correo: String, telefono: String,
    contrasena: String, confirmarContrasena: String
): Pair<Boolean, String> {
    val regexLetras = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")

    if (nombre.isBlank()) return false to "Por favor ingresa tu nombre."
    if (!nombre.matches(regexLetras)) return false to "El nombre solo puede contener letras."
    if (apellido.isBlank()) return false to "Por favor ingresa tu apellido."
    if (!apellido.matches(regexLetras)) return false to "El apellido solo puede contener letras."

    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    if (!correo.matches(emailRegex)) return false to "Correo electrónico inválido."

    val phoneRegex = Regex("^\\+569\\d{8}\$")
    if (!telefono.matches(phoneRegex))
        return false to "Teléfono inválido. Debe tener el formato +569XXXXXXXX."

    val tieneNumero = contrasena.any { it.isDigit() }
    val tieneMayuscula = contrasena.any { it.isUpperCase() }
    val tieneMinuscula = contrasena.any { it.isLowerCase() }
    val tieneEspecial = contrasena.any { "!@#\$%^&*()-_=+{}[]|:;'<>,.?/`~".contains(it) }

    if (contrasena.length < 8)
        return false to "La contraseña debe tener al menos 8 caracteres."
    if (!(tieneNumero && tieneMayuscula && tieneMinuscula && tieneEspecial))
        return false to "La contraseña debe incluir mayúsculas, minúsculas, números y símbolos."
    if (contrasena != confirmarContrasena)
        return false to "Las contraseñas no coinciden."

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
    val coroutineScope = rememberCoroutineScope()

    var nombre by rememberSaveable { mutableStateOf("") }

    var apellido by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmarContrasena by rememberSaveable { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var hayError by remember { mutableStateOf(false) }
    var exito by remember { mutableStateOf(false) }
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
                    Text(
                        text = "Crear cuenta",
                        fontSize = 24.sp,
                        color = MyCarBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MyCarTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
                    Spacer(modifier = Modifier.height(12.dp))
                    MyCarTextField(value = apellido, onValueChange = { apellido = it }, label = "Apellido")
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
                        label = "Teléfono (+569xxxxxxxx)",
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
                        value = confirmarContrasena,
                        onValueChange = { confirmarContrasena = it },
                        label = "Confirmar contraseña",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MyCarButton(text = "Registrarse") {
                        val (esValido, msg) = validarCampos(nombre, apellido, correo, telefono, contrasena, confirmarContrasena)
                        if (!esValido) {
                            mensaje = msg
                            hayError = true
                            exito = false
                            return@MyCarButton
                        }

                        isLoading = true
                        mensaje = ""

                        // Llamada con callback onResult (coincide con tu ViewModel)
                        userViewModel.registerUser(
                            name = nombre,
                            lastName = apellido,
                            email = correo,
                            password = contrasena,
                            phone = telefono
                        ) { guardado ->
                            isLoading = false
                            if (guardado) {
                                mensaje = "Usuario registrado exitosamente 🎉"
                                hayError = false
                                exito = true
                                onRegistered()
                            } else {
                                mensaje = "El correo ya está registrado."
                                hayError = true
                                exito = false
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    MyCarOutlinedButton(text = "¿Ya tienes cuenta? Inicia sesión", onClick = onGoLogin)

                    Spacer(modifier = Modifier.height(20.dp))
                    if (mensaje.isNotEmpty()) {
                        MyCarSnackbar(
                            message = mensaje,
                            color = when {
                                exito -> MyCarGreen
                                hayError -> MyCarRed
                                else -> MyCarBlue
                            }
                        )
                    }
                }
            }
        }
    }
}
