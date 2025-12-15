package com.example.mycar

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycar.network.dto.*
import com.example.mycar.repository.AuthRepository
import com.example.mycar.repository.VehicleRepository
import com.example.mycar.repository.MaintenanceRepository
import com.example.mycar.repository.ExpenseRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // ========= REPOSITORIOS =========
    private val authRepository = AuthRepository()
    private val vehicleRepository = VehicleRepository()
    private val maintenanceRepository = MaintenanceRepository()
    private val expenseRepository = ExpenseRepository()

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

    // ========= MANTENIMIENTO DESDE API =========
    var maintenanceApiList = mutableStateListOf<MaintenanceResponse>()

    // ========= EXPENSE DESDE API =========
    var expenseApiList = mutableStateListOf<ExpenseResponse>()

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
        maintenanceApiList.clear()
        expenseApiList.clear()
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

    fun updateVehicle(
        id: Long,
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

                val updated = vehicleRepository.update(id, request)

                val index = vehicles.indexOfFirst { it.id == id }
                if (index != -1) {
                    vehicles[index] = updated
                }

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
    //                        MANTENIMIENTO - API
    // =====================================================================

    fun loadMaintenanceByVehicle(vehicleId: Long) {
        viewModelScope.launch {
            try {
                val list = maintenanceRepository.listByVehicle(vehicleId)
                maintenanceApiList.clear()
                maintenanceApiList.addAll(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createMaintenance(
        vehicleId: Long,
        vehiclePlate: String,
        type: String,
        date: String,
        km: Int,
        notes: String?,
        cost: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = MaintenanceRequest(
                    vehicleId = vehicleId,
                    vehiclePlate = vehiclePlate,
                    type = type,
                    date = date,
                    km = km,
                    notes = notes,
                    cost = cost
                )

                val created = maintenanceRepository.create(request)

                maintenanceApiList.add(0, created)

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun updateMaintenance(
        id: Long,
        vehicleId: Long,
        vehiclePlate: String,
        type: String,
        date: String,
        km: Int,
        notes: String?,
        cost: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = MaintenanceRequest(
                    vehicleId = vehicleId,
                    vehiclePlate = vehiclePlate,
                    type = type,
                    date = date,
                    km = km,
                    notes = notes,
                    cost = cost
                )

                val updated = maintenanceRepository.update(id, request)

                val index = maintenanceApiList.indexOfFirst { it.id == id }
                if (index != -1) {

                    maintenanceApiList.removeAt(index)
                    maintenanceApiList.add(0, updated)
                }

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun deleteMaintenanceApi(id: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                maintenanceRepository.delete(id)
                maintenanceApiList.removeAll { it.id == id }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    // =====================================================================
    //                            EXPENSE - API
    // =====================================================================

    fun loadExpensesByVehicle(vehicleId: Long) {
        viewModelScope.launch {
            try {
                val list = expenseRepository.listByVehicle(vehicleId)
                expenseApiList.clear()
                expenseApiList.addAll(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createExpense(
        vehicleId: Long,
        vehiclePlate: String,
        category: String,
        type: String,
        date: String,
        amount: Int,
        km: Int?,
        notes: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = ExpenseRequest(
                    vehicleId = vehicleId,
                    vehiclePlate = vehiclePlate,
                    category = category,
                    type = type,
                    date = date,
                    amount = amount,
                    km = km,
                    notes = notes
                )

                val created = expenseRepository.create(request)

                expenseApiList.add(0, created)

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun updateExpense(
        id: Long,
        vehicleId: Long,
        vehiclePlate: String,
        category: String,
        type: String,
        date: String,
        amount: Int,
        km: Int?,
        notes: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = ExpenseRequest(
                    vehicleId = vehicleId,
                    vehiclePlate = vehiclePlate,
                    category = category,
                    type = type,
                    date = date,
                    amount = amount,
                    km = km,
                    notes = notes
                )

                val updated = expenseRepository.update(id, request)

                val index = expenseApiList.indexOfFirst { it.id == id }
                if (index != -1) {

                    expenseApiList.removeAt(index)
                    expenseApiList.add(0, updated)
                }

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun deleteExpenseApi(id: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                expenseRepository.delete(id)
                expenseApiList.removeAll { it.id == id }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
