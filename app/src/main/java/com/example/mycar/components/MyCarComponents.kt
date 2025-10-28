package com.example.mycar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycar.ui.theme.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.draw.scale


// ---------------------------------------------------------------------------
// HEADER GLOBAL
// ---------------------------------------------------------------------------
@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = MyCarBlue)
            }
        }
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MyCarBlue,
            modifier = Modifier.padding(start = if (onBack != null) 8.dp else 0.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// BOTÓN PRINCIPAL
// ---------------------------------------------------------------------------
@Composable
fun MyCarButton(
    text: String,
    icon: ImageVector? = Icons.Filled.ArrowForward,
    color: Color = MyCarBlue,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, color = Color.White, fontSize = 18.sp)
        }
    }
}

// ---------------------------------------------------------------------------
// BOTÓN SECUNDARIO
// ---------------------------------------------------------------------------
@Composable
fun MyCarOutlinedButton(
    text: String,
    icon: ImageVector? = null,
    color: Color = MyCarBlue,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = text, fontSize = 16.sp)
        }
    }
}

// ---------------------------------------------------------------------------
// TARJETA DE INFORMACIÓN
// ---------------------------------------------------------------------------
@Composable
fun InfoCard(
    title: String,
    content: String,
    icon: ImageVector? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = MyCarBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MyCarBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(color = MyCarBlack)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// CAMPO DE TEXTO ESTÁNDAR
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    isPassword: Boolean = false
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = MyCarBlue) },
        singleLine = singleLine,
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(icon, contentDescription = "Mostrar u ocultar contraseña", tint = MyCarBlue)
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        textStyle = TextStyle(color = MyCarBlack, fontSize = 16.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MyCarBlue,
            unfocusedBorderColor = MyCarBlue.copy(alpha = 0.5f),
            cursorColor = MyCarBlue,
            focusedLabelColor = MyCarBlue,
            unfocusedLabelColor = MyCarBlack,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// ---------------------------------------------------------------------------
// SNACKBAR PERSONALIZADO
// ---------------------------------------------------------------------------
@Composable
fun MyCarSnackbar(
    message: String,
    color: Color = MyCarGreen
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Medium)
        )
    }
}


//  ANIMACIÓN DE CARGA MEJORADA
@Composable
fun MyCarLoading(text: String = "Cargando") {
    var dotCount by remember { mutableStateOf(0) }

    // Animación de puntos (…)
    LaunchedEffect(Unit) {
        while (true) {
            dotCount = (dotCount + 1) % 4
            kotlinx.coroutines.delay(400)
        }
    }

    // Animación suave del spinner (zoom in/out)
    val scale by animateFloatAsState(
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )

    // Fondo con gradiente
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MyCarLightBlue, Color.White)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Spinner animado
        CircularProgressIndicator(
            color = MyCarBlue,
            modifier = Modifier
                .size(60.dp)
                .scale(scale)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto con puntos animados
        Text(
            text = "$text${".".repeat(dotCount)}",
            color = MyCarBlue,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}
