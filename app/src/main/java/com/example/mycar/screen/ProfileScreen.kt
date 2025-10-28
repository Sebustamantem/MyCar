package com.example.mycar.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mycar.UserViewModel
import com.example.mycar.components.MyCarOutlinedButton
import com.example.mycar.ui.theme.MyCarBlue
import com.example.mycar.ui.theme.MyCarLightBlue

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val backgroundGradient = Brush.verticalGradient(listOf(MyCarLightBlue, Color.White))
    val name by userViewModel.userName
    val lastName by userViewModel.userLastName
    val email by userViewModel.userEmail
    val phone by userViewModel.userPhone

    // Estado de la foto (persistente en ViewModel)
    val photo by userViewModel.profilePhoto

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            userViewModel.profilePhoto.value = bitmap
        }
    }

    // Permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text("Perfil", style = MaterialTheme.typography.headlineSmall, color = MyCarBlue)
        Spacer(modifier = Modifier.height(20.dp))

        // Foto de perfil circular
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (photo != null) {
                Image(
                    bitmap = photo!!.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Sin foto",
                    tint = MyCarBlue,
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón cámara
        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Abrir cámara")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tomar foto")
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Datos del usuario con tu formato
        ProfileInfoCard(title = "Nombre", content = "$name $lastName")
        Spacer(modifier = Modifier.height(12.dp))
        ProfileInfoCard(title = "Correo", content = if (email.isEmpty()) "No disponible" else email)
        Spacer(modifier = Modifier.height(12.dp))
        ProfileInfoCard(title = "Teléfono", content = if (phone.isEmpty()) "No registrado" else phone)

        Spacer(modifier = Modifier.height(40.dp))

        // Cerrar sesión
        MyCarOutlinedButton(text = "Cerrar sesión") {
            userViewModel.logout()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
}

@Composable
fun ProfileInfoCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
