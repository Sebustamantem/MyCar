package com.example.mycar

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.mycar.data.*
import com.example.mycar.model.AlertRecord
import com.example.mycar.model.VehicleData
import com.example.mycar.repository.AlertRepository
import com.example.mycar.repository.UserRepository
import com.example.mycar.repository.VehicleRepository
import com.example.mycar.network.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // ============================================================
    // ROOM DATABASE
    // ============================================================
    private val db = Room.databaseBuilder(
        application,
        MyCarDatabase::class.java,
        "mycar_database"
    ).fallbackToDestructiveMigration().build()

    private val userDao = db.userDao()
    private val vehicleDao = db.vehicleDao()
    private val alertDao = db.alertDao()

    // ============================================================
    // REPOSITORIES (Retrofit)
    // ============================================================
    private val userRepo = UserRepository()
    private val vehicleRepo = VehicleRepository()
    private val alertRepo = AlertRepository()

    // ============================================================
    // USER SESSION
    // ============================================================
    var userName = mutableStateOf("")
    var userLastName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userPhone = mutableStateOf("")
    var isLoggedIn = mutableStateOf(false)

    var profilePhoto = mutableStateOf<android.graphics.Bitmap?>(null)

    // ============================================================
    // VEHICLES
    // ============================================================
    var vehicles = mutableStateListOf<VehicleData>()

    // ============================================================
    // ALERTS
    // ============================================================
    var alerts = mutableStateListOf<AlertRecord>()

    // ============================================================
    // AUTH: REGISTER
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
            try {
                val request = UserRequest(
                    name = name,
                    lastName = lastName,
                    email = email,
                    password = password,
                    phone = phone
                )

                val response = userRepo.register(request)

                // Guardar en Room
                userDao.insert(
                    UserEntity(
                        email = response.email,
                        name = response.name,
                        lastName = response.lastName,
                        phone = response.phone
                    )
                )

                withContext(Dispatchers.Main) { onResult(true) }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    // ============================================================
    // AUTH: LOGIN
    // ============================================================
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepo.login(email, password)

                if (response == null) {
                    withContext(Dispatchers.Main) { onResult(false) }
                    return@launch
                }

                // Guardar sesión
                userDao.insert(
                    UserEntity(
                        email = response.email,
                        name = response.name,
                        lastName = response.lastName,
                        phone = response.phone
                    )
                )

                withContext(Dispatchers.Main) {
                    userName.value = response.name
                    userLastName.value = response.lastName
                    userEmail.value = response.email
                    userPhone.value = response.phone
                    isLoggedIn.value = true
                    onResult(true)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
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
        alerts.clear()
        profilePhoto.value = null
    }

    // ============================================================
    // VEHICLES: LOAD FROM API + SAVE IN ROOM
    // ============================================================
    fun loadVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiList = vehicleRepo.getVehicles(userEmail.value)

                // Sync Room
                vehicleDao.clear()
                apiList.forEach {
                    vehicleDao.insert(
                        VehicleEntity(
                            plate = it.plate,
                            ownerEmail = it.ownerEmail,
                            brand = it.brand,
                            model = it.model,
                            year = it.year,
                            km = it.km,
                            soapDate = it.soapDate,
                            permisoCirculacionDate = it.permisoCirculacionDate,
                            revisionTecnicaDate = it.revisionTecnicaDate
                        )
                    )
                }

                val mapped = apiList.map {
                    VehicleData(
                        it.brand, it.model, it.year, it.plate, it.km,
                        it.soapDate, it.permisoCirculacionDate, it.revisionTecnicaDate
                    )
                }

                withContext(Dispatchers.Main) {
                    vehicles.clear()
                    vehicles.addAll(mapped)
                }

            } catch (e: Exception) {
                val local = vehicleDao.getAllByEmail(userEmail.value)
                val mapped = local.map {
                    VehicleData(
                        it.brand, it.model, it.year, it.plate, it.km,
                        it.soapDate, it.permisoCirculacionDate, it.revisionTecnicaDate
                    )
                }
                withContext(Dispatchers.Main) {
                    vehicles.clear()
                    vehicles.addAll(mapped)
                }
            }
        }
    }

    fun addVehicle(vehicle: VehicleData, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = VehicleRequest(
                    ownerEmail = userEmail.value,
                    brand = vehicle.brand,
                    model = vehicle.model,
                    year = vehicle.year,
                    plate = vehicle.plate,
                    km = vehicle.km,
                    soapDate = vehicle.soapDate,
                    permisoCirculacionDate = vehicle.permisoCirculacionDate,
                    revisionTecnicaDate = vehicle.revisionTecnicaDate
                )

                val response = vehicleRepo.addVehicle(request)

                // Guardar local
                vehicleDao.insert(
                    VehicleEntity(
                        plate = response.plate,
                        ownerEmail = response.ownerEmail,
                        brand = response.brand,
                        model = response.model,
                        year = response.year,
                        km = response.km,
                        soapDate = response.soapDate,
                        permisoCirculacionDate = response.permisoCirculacionDate,
                        revisionTecnicaDate = response.revisionTecnicaDate
                    )
                )

                loadVehicles()
                withContext(Dispatchers.Main) { onResult(true) }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun removeVehicle(vehicle: VehicleData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                vehicleRepo.deleteVehicle(vehicle.plate)
            } catch (_: Exception) {}

            vehicleDao.deleteByPlate(vehicle.plate)
            loadVehicles()
        }
    }

    // ============================================================
    // ALERTAS
    // ============================================================
    fun loadAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiList = alertRepo.getAlerts(userEmail.value)

                alertDao.clear()
                apiList.forEach {
                    alertDao.insert(
                        AlertEntity(
                            id = it.id,
                            userEmail = it.userEmail,
                            title = it.title,
                            message = it.message,
                            date = it.date
                        )
                    )
                }

                val mapped = apiList.map {
                    AlertRecord(it.id, it.title, it.message, it.date)
                }

                withContext(Dispatchers.Main) {
                    alerts.clear()
                    alerts.addAll(mapped)
                }

            } catch (e: Exception) {
                val local = alertDao.getAll()
                val mapped = local.map {
                    AlertRecord(it.id, it.title, it.message, it.date)
                }

                withContext(Dispatchers.Main) {
                    alerts.clear()
                    alerts.addAll(mapped)
                }
            }
        }
    }

    fun addAlert(title: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val request = AlertRequest(
                userEmail = userEmail.value,
                title = title,
                message = message,
                date = date
            )

            try {
                val resp = alertRepo.addAlert(request)

                alertDao.insert(
                    AlertEntity(
                        id = resp.id,
                        userEmail = resp.userEmail,
                        title = resp.title,
                        message = resp.message,
                        date = resp.date
                    )
                )

            } catch (_: Exception) {}

            loadAlerts()
        }
    }

    fun removeAlert(alert: AlertRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            try { alertRepo.deleteAlert(alert.id) } catch (_: Exception) {}
            alertDao.deleteById(alert.id)
            loadAlerts()
        }
    }
}
