package com.example.mycar

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycar.models.VehicleData
import com.example.mycar.models.MaintenanceRecord
import com.example.mycar.models.AlertRecord
import com.example.mycar.repository.AuthRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    // ======== Datos Usuario ========
    var userId = mutableStateOf<Long?>(null)
    var userName = mutableStateOf("")
    var userLastName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userPhone = mutableStateOf("")
    var token = mutableStateOf("")
    var isLoggedIn = mutableStateOf(false)

    var profilePhoto = mutableStateOf<Bitmap?>(null)

    // ======== Datos locales ========
    var vehicles = mutableStateListOf<VehicleData>()
    var maintenanceList = mutableStateListOf<MaintenanceRecord>()
    var alerts = mutableStateListOf<AlertRecord>()

    // ======== LOGIN ========
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)

            result.onSuccess { user ->
                userId.value = user.id
                userName.value = user.name
                userLastName.value = user.lastName
                userEmail.value = user.email
                userPhone.value = user.phone
                token.value = user.token
                isLoggedIn.value = true

                onResult(true)
            }

            result.onFailure {
                onResult(false)
            }
        }
    }

    // ======== REGISTER ========
    fun registerUser(
        name: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.register(name, lastName, email, password, phone)

            result.onSuccess { user ->
                userId.value = user.id
                userName.value = user.name
                userLastName.value = user.lastName
                userEmail.value = user.email
                userPhone.value = user.phone
                token.value = user.token
                isLoggedIn.value = true

                onResult(true)
            }

            result.onFailure {
                onResult(false)
            }
        }
    }

    // ======== LOGOUT ========
    fun logout() {
        userId.value = null
        userName.value = ""
        userLastName.value = ""
        userEmail.value = ""
        userPhone.value = ""
        token.value = ""
        isLoggedIn.value = false

        vehicles.clear()
        maintenanceList.clear()
        alerts.clear()
        profilePhoto.value = null
    }

    // ======== Vehículos ========
    fun addVehicle(vehicle: VehicleData, onResult: (Boolean) -> Unit = {}) {
        vehicles.add(vehicle)
        checkVehicleAlerts(vehicle)
        onResult(true)
    }

    fun removeVehicle(vehicle: VehicleData) {
        vehicles.remove(vehicle)
        alerts.removeAll { it.title.contains(vehicle.plate, ignoreCase = true) }
    }

    fun loadVehicles() {}

    // ======== Alertas ========
    fun addAlert(title: String, message: String) {
        alerts.add(
            AlertRecord(
                title = title,
                message = message,
                date = getTodayDate()
            )
        )
    }

    fun removeAlert(alert: AlertRecord) {
        alerts.remove(alert)
    }

    fun loadAlerts() {}

    private fun checkVehicleAlerts(vehicle: VehicleData) {
        if (daysUntil(vehicle.soapDate) <= 15)
            addAlert("SOAP por vencer", "El SOAP de ${vehicle.plate} vence el ${vehicle.soapDate}")

        if (daysUntil(vehicle.permisoCirculacionDate) <= 15)
            addAlert("Permiso por vencer", "El permiso vence el ${vehicle.permisoCirculacionDate}")

        if (daysUntil(vehicle.revisionTecnicaDate) <= 15)
            addAlert("Revisión técnica por vencer", "La revisión vence el ${vehicle.revisionTecnicaDate}")
    }

    // ======== Mantenimiento ========
    fun addMaintenance(record: MaintenanceRecord) {
        maintenanceList.add(record)
    }

    fun removeMaintenance(record: MaintenanceRecord) {
        maintenanceList.remove(record)
    }

    // ======== Utilidades ========
    private fun getTodayDate(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun daysUntil(date: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val target = sdf.parse(date)
            val now = Date()
            ((target.time - now.time) / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }
}
