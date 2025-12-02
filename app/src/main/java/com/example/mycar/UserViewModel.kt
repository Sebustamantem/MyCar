package com.example.mycar

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycar.models.MaintenanceRecord
import com.example.mycar.models.AlertRecord
import com.example.mycar.network.dto.VehicleRequest
import com.example.mycar.network.dto.VehicleResponse
import com.example.mycar.repository.AuthRepository
import com.example.mycar.repository.VehicleRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // ========= REPOSITORIOS =========
    private val authRepository = AuthRepository()
    private val vehicleRepository = VehicleRepository()

    // ========= DATOS USUARIO =========
    var userId = mutableStateOf<Long?>(null)
    var userName = mutableStateOf("")
    var userLastName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userPhone = mutableStateOf("")
    var token = mutableStateOf("")
    var isLoggedIn = mutableStateOf(false)

    var profilePhoto = mutableStateOf<Bitmap?>(null)

    // ========= VEHÍCULOS (desde API) =========
    var vehicles = mutableStateListOf<VehicleResponse>()

    // ========= MANTENIMIENTO LOCAL =========
    var maintenanceList = mutableStateListOf<MaintenanceRecord>()

    // ========= ALERTAS LOCALES =========
    var alerts = mutableStateListOf<AlertRecord>()


    // =====================================================================
    //                                AUTH
    // =====================================================================

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

            result.onFailure { onResult(false) }
        }
    }

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

            result.onFailure { onResult(false) }
        }
    }

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


    // =====================================================================
    //                             VEHICLES API
    // =====================================================================

    fun loadVehicles() {
        viewModelScope.launch {
            try {
                val user = userId.value ?: return@launch
                val list = vehicleRepository.getByUser(user)
                vehicles.clear()
                vehicles.addAll(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createVehicle(
        brand: String,
        model: String,
        year: Int,
        plate: String,
        km: Int,
        soap: String,
        permiso: String,
        revision: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = userId.value ?: return@launch

                val request = VehicleRequest(
                    brand = brand,
                    model = model,
                    year = year,
                    plate = plate,
                    km = km,
                    soapDate = soap,
                    permisoCirculacionDate = permiso,
                    revisionTecnicaDate = revision,
                    userId = user
                )

                val created = vehicleRepository.create(request)
                vehicles.add(created)

                onResult(true)

            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun deleteVehicle(id: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                vehicleRepository.delete(id)
                vehicles.removeAll { it.id == id }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }


    // =====================================================================
    //                             ALERTAS LOCALES
    // =====================================================================

    fun loadAlerts() {
        // puedes cargar desde BD si más adelante quieres
    }

    fun removeAlert(alert: AlertRecord) {
        alerts.remove(alert)
    }

    fun addAlert(title: String, message: String) {
        alerts.add(
            AlertRecord(
                title = title,
                message = message,
                date = getTodayDate()
            )
        )
    }


    // =====================================================================
    //                      MANTENIMIENTO LOCAL (NO API)
    // =====================================================================

    fun addMaintenance(record: MaintenanceRecord) {
        maintenanceList.add(record)
    }

    fun removeMaintenance(record: MaintenanceRecord) {
        maintenanceList.remove(record)
    }


    // =====================================================================
    //                                UTILIDADES
    // =====================================================================

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
