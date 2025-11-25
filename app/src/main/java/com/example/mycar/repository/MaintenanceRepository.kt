package com.example.mycar.repository

import com.example.mycar.network.RetrofitInstance
import com.example.mycar.network.dto.MaintenanceRequest

class MaintenanceRepository {

    private val api = RetrofitInstance.api

    suspend fun getMaintenance(email: String) =
        api.getMaintenance(email)

    suspend fun addMaintenance(req: MaintenanceRequest) =
        api.addMaintenance(req)

    suspend fun deleteMaintenance(id: Long) =
        api.deleteMaintenance(id)
}
