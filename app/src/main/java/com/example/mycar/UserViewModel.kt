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
import com.example.mycar.model.MaintenanceRecord
import com.example.mycar.network.dto.*
import com.example.mycar.repository.AlertRepository
import com.example.mycar.repository.MaintenanceRepository
import com.example.mycar.repository.UserRepository
import com.example.mycar.repository.VehicleRepository
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
    private val maintenanceDao = db.maintenanceDao()

    // ============================================================
    // REPOSITORIES (Retrofit)
    // ============================================================
    private val userRepo = UserRepository()
    private val vehicleRepo = VehicleRepository()
    private val alertRepo = AlertRepository()
    private val maintenanceRepo = MaintenanceRepository()

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
    // STATE LISTS
    // ============================================================
    var vehicles = mutableStateListOf<VehicleData>()
    var alerts = mutableStateListOf<AlertRecord>()
    var maintenanceList = mutableStateListOf<MaintenanceRecord>()


    // ============================================================
    // REGISTER
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
                val request = RegisterRequest(
                    name = name,
                    lastName = lastName,
                    email = email,
                    password = password,
                    phone = phone
                )

                val response = userRepo.register(request)

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
    // LOGIN
    // ============================================================
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepo.login(email, password)

                if (response == null) {
                    withContext(Dispatchers.Main) { onResult(false) }
                    return@launch
                }

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
        maintenanceList.clear()

        profilePhoto.value = null
    }


    // ============================================================
    // VEHICLES
    // ============================================================
    fun loadVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiList = vehicleRepo.getVehicles(userEmail.value)

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
                        brand = it.brand,
                        model = it.model,
                        year = it.year,
                        plate = it.plate,
                        km = it.km,
                        soapDate = it.soapDate,
                        permisoCirculacionDate = it.permisoCirculacionDate,
                        revisionTecnicaDate = it.revisionTecnicaDate
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
                        brand = it.brand,
                        model = it.model,
                        year = it.year,
                        plate = it.plate,
                        km = it.km,
                        soapDate = it.soapDate,
                        permisoCirculacionDate = it.permisoCirculacionDate,
                        revisionTecnicaDate = it.revisionTecnicaDate
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
            try { vehicleRepo.deleteVehicle(vehicle.plate) } catch (_: Exception) {}

            vehicleDao.deleteByPlate(vehicle.plate)
            loadVehicles()
        }
    }


    // ============================================================
    // ALERTS
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


    // ============================================================
    // MAINTENANCE
    // ============================================================
    fun loadMaintenance() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiList = maintenanceRepo.getMaintenance(userEmail.value)

                maintenanceDao.clear()

                apiList.forEach {
                    maintenanceDao.insert(
                        MaintenanceEntity(
                            id = it.id,
                            userEmail = userEmail.value,
                            type = it.type,
                            vehiclePlate = it.vehiclePlate,
                            date = it.date,
                            km = it.km,
                            notes = it.notes
                        )
                    )
                }

                val mapped = apiList.map {
                    MaintenanceRecord(
                        id = it.id,
                        type = it.type,
                        vehiclePlate = it.vehiclePlate,
                        date = it.date,
                        km = it.km,
                        notes = it.notes
                    )
                }

                withContext(Dispatchers.Main) {
                    maintenanceList.clear()
                    maintenanceList.addAll(mapped)
                }

            } catch (e: Exception) {

                val local = maintenanceDao.getAll()

                val mapped = local.map {
                    MaintenanceRecord(
                        id = it.id,
                        type = it.type,
                        vehiclePlate = it.vehiclePlate,
                        date = it.date,
                        km = it.km,
                        notes = it.notes
                    )
                }

                withContext(Dispatchers.Main) {
                    maintenanceList.clear()
                    maintenanceList.addAll(mapped)
                }
            }
        }
    }


    fun addMaintenance(record: MaintenanceRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = MaintenanceRequest(
                type = record.type,
                vehiclePlate = record.vehiclePlate,
                date = record.date,
                km = record.km,
                notes = record.notes
            )

            try {
                maintenanceRepo.addMaintenance(request)
            } catch (_: Exception) {}

            loadMaintenance()
        }
    }


    fun removeMaintenance(record: MaintenanceRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            try { maintenanceRepo.deleteMaintenance(record.id) } catch (_: Exception) {}

            maintenanceDao.deleteById(record.id)
            loadMaintenance()
        }
    }
}
