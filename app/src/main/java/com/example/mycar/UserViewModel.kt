package com.example.mycar

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.mycar.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // ============================================================
    //  BASE DE DATOS ROOM
    // ============================================================
    private val db = Room.databaseBuilder(
        application,
        MyCarDatabase::class.java,
        "mycar_database"
    )
        //  Recrear base de datos automáticamente si cambia el esquema
        .fallbackToDestructiveMigration()
        .build()

    private val userDao = db.userDao()
    private val vehicleDao = db.vehicleDao()
    private val maintenanceDao = db.maintenanceDao()
    private val alertDao = db.alertDao()

    // ============================================================
    // USUARIO ACTUAL
    // ============================================================
    var userName = mutableStateOf("")
    var userLastName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userPhone = mutableStateOf("")
    var isLoggedIn = mutableStateOf(false)

    // FOTO DE PERFIL (se mantiene en memoria durante la sesión)
    var profilePhoto = mutableStateOf<android.graphics.Bitmap?>(null)


    // ============================================================
    // VEHÍCULOS
    // ============================================================
    var vehicles = mutableStateListOf<VehicleData>()

    // ============================================================
    // MANTENIMIENTOS
    // ============================================================
    var maintenanceList = mutableStateListOf<MaintenanceRecord>()

    // ============================================================
    //  ALERTAS
    // ============================================================
    var alerts = mutableStateListOf<AlertRecord>()

    // ============================================================
    // REGISTRO / LOGIN
    // ============================================================
    fun registerUser(
        name: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = userDao.findByEmail(email)
            if (existing != null) {
                withContext(Dispatchers.Main) { onResult(false) }
            } else {
                userDao.insert(
                    UserEntity(
                        name = name,
                        lastName = lastName,
                        email = email,
                        password = password,
                        phone = phone
                    )
                )
                withContext(Dispatchers.Main) { onResult(true) }
            }
        }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.login(email, password)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    userName.value = user.name
                    userLastName.value = user.lastName
                    userEmail.value = user.email
                    userPhone.value = user.phone
                    isLoggedIn.value = true
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
        }
    }

    fun logout() {
        userName.value = ""
        userLastName.value = ""
        userEmail.value = ""
        userPhone.value = ""
        isLoggedIn.value = false
        vehicles.clear()
        maintenanceList.clear()
        alerts.clear()
        profilePhoto.value = null // Limpia la imagen de perfil al cerrar sesión
    }

    // ============================================================
    // VEHÍCULOS
    // ============================================================
    fun addVehicle(vehicle: VehicleData, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleDao.insert(
                VehicleEntity(
                    brand = vehicle.brand,
                    model = vehicle.model,
                    year = vehicle.year,
                    plate = vehicle.plate,
                    km = vehicle.km,
                    soapDate = vehicle.soapDate,
                    permisoCirculacionDate = vehicle.permisoCirculacionDate,
                    revisionTecnicaDate = vehicle.revisionTecnicaDate
                )
            )

            val updated = vehicleDao.getAll()
            withContext(Dispatchers.Main) {
                vehicles.clear()
                val converted = updated.map {
                    VehicleData(
                        it.brand, it.model, it.year, it.plate, it.km,
                        it.soapDate, it.permisoCirculacionDate, it.revisionTecnicaDate
                    )
                }
                vehicles.addAll(converted)
                onResult(true)

                // Crear alertas automáticas al registrar vehículo
                converted.forEach { checkVehicleAlerts(it) }
            }
        }
    }

    fun removeVehicle(vehicle: VehicleData) {
        viewModelScope.launch(Dispatchers.IO) {
            vehicleDao.deleteByPlate(vehicle.plate)
            val updated = vehicleDao.getAll()
            withContext(Dispatchers.Main) {
                vehicles.clear()
                vehicles.addAll(updated.map {
                    VehicleData(
                        it.brand, it.model, it.year, it.plate, it.km,
                        it.soapDate, it.permisoCirculacionDate, it.revisionTecnicaDate
                    )
                })
            }
        }
    }

    fun loadVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = vehicleDao.getAll()
            withContext(Dispatchers.Main) {
                vehicles.clear()
                val converted = all.map {
                    VehicleData(
                        it.brand,
                        it.model,
                        it.year,
                        it.plate,
                        it.km,
                        it.soapDate,
                        it.permisoCirculacionDate,
                        it.revisionTecnicaDate
                    )
                }
                vehicles.addAll(converted)

                // Revisar vencimientos al cargar
                converted.forEach { v -> checkVehicleAlerts(v) }
            }
        }
    }

    // ============================================================
    // MANTENIMIENTOS
    // ============================================================
    fun addMaintenance(record: MaintenanceRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            maintenanceDao.insert(
                MaintenanceEntity(
                    vehiclePlate = record.vehiclePlate,
                    type = record.type,
                    date = record.date,
                    km = record.km,
                    notes = record.notes
                )
            )
            val updated = maintenanceDao.getAll()
            withContext(Dispatchers.Main) {
                maintenanceList.clear()
                maintenanceList.addAll(updated.map {
                    MaintenanceRecord(it.vehiclePlate, it.type, it.date, it.km, it.notes)
                })
            }
        }
    }

    fun removeMaintenance(record: MaintenanceRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            maintenanceDao.delete(
                MaintenanceEntity(
                    vehiclePlate = record.vehiclePlate,
                    type = record.type,
                    date = record.date,
                    km = record.km,
                    notes = record.notes
                )
            )
            val updated = maintenanceDao.getAll()
            withContext(Dispatchers.Main) {
                maintenanceList.clear()
                maintenanceList.addAll(updated.map {
                    MaintenanceRecord(it.vehiclePlate, it.type, it.date, it.km, it.notes)
                })
            }
        }
    }

    fun loadMaintenance() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = maintenanceDao.getAll()
            withContext(Dispatchers.Main) {
                maintenanceList.clear()
                maintenanceList.addAll(all.map {
                    MaintenanceRecord(it.vehiclePlate, it.type, it.date, it.km, it.notes)
                })
            }
        }
    }

    // ============================================================
    // ALERTAS
    // ============================================================
    fun addAlert(title: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val alert = AlertEntity(
                title = title,
                message = message,
                date = getTodayDate()
            )
            alertDao.insert(alert)
            val updated = alertDao.getAll()
            withContext(Dispatchers.Main) {
                alerts.clear()
                alerts.addAll(updated.map { AlertRecord(it.id, it.title, it.message, it.date) })
            }
        }
    }

    fun removeAlert(alert: AlertRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            alertDao.deleteById(alert.id)
            val updated = alertDao.getAll()
            withContext(Dispatchers.Main) {
                alerts.clear()
                alerts.addAll(updated.map { AlertRecord(it.id, it.title, it.message, it.date) })
            }
        }
    }

    fun loadAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = alertDao.getAll()
            withContext(Dispatchers.Main) {
                alerts.clear()
                alerts.addAll(all.map { AlertRecord(it.id, it.title, it.message, it.date) })
            }
        }
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun daysUntil(date: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val target = sdf.parse(date)
            val now = Date()
            val diff = target.time - now.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    // ============================================================
    // ALERTAS AUTOMÁTICAS SEGÚN FECHAS
    // ============================================================
    private fun checkVehicleAlerts(vehicle: VehicleData) {
        if (daysUntil(vehicle.soapDate) in 0..15) {
            addAlert("SOAP por vencer", "El SOAP de ${vehicle.plate} vence el ${vehicle.soapDate}.")
        }
        if (daysUntil(vehicle.permisoCirculacionDate) in 0..15) {
            addAlert(
                "Permiso de circulación por vencer",
                "El permiso de ${vehicle.plate} vence el ${vehicle.permisoCirculacionDate}."
            )
        }
        if (daysUntil(vehicle.revisionTecnicaDate) in 0..15) {
            addAlert(
                "Revisión técnica próxima",
                "La revisión técnica de ${vehicle.plate} vence el ${vehicle.revisionTecnicaDate}."
            )
        }
    }
}
