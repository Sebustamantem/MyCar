package com.example.mycar.repository

import com.example.mycar.network.ApiClient
import com.example.mycar.network.dto.MaintenanceRequest

class MaintenanceRepository {

    private val api = ApiClient.maintenanceService

    suspend fun create(request: MaintenanceRequest) =
        api.createMaintenance(request)

    suspend fun listByVehicle(vehicleId: Long) =
        api.getByVehicle(vehicleId)

    suspend fun update(id: Long, request: MaintenanceRequest) =
        api.updateMaintenance(id, request)


    suspend fun delete(id: Long) =
        api.deleteMaintenance(id)
}
