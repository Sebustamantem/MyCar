package com.example.mycar.repository

import com.example.mycar.network.ApiService
import com.example.mycar.network.RetrofitClient

class MyCarRepository {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    // AUTH
    suspend fun login(email: String, pass: String) =
        api.login(LoginRequest(email, pass))

    suspend fun register(req: RegisterRequest) =
        api.register(req)


    // VEHICLES
    suspend fun getVehicles(email: String) =
        api.getVehicles(email)

    suspend fun addVehicle(req: VehicleRequest) =
        api.addVehicle(req)

    suspend fun deleteVehicle(plate: String) =
        api.deleteVehicle(plate)


    // MAINTENANCE
    suspend fun getMaintenance(email: String) =
        api.getMaintenance(email)

    suspend fun addMaintenance(req: MaintenanceRequest) =
        api.addMaintenance(req)

    suspend fun deleteMaintenance(id: Long) =
        api.deleteMaintenance(id)
}
