package com.example.mycar.repository

import com.example.mycar.network.RetrofitInstance
import com.example.mycar.network.dto.VehicleRequest

class VehicleRepository {

    private val api = RetrofitInstance.api

    suspend fun getVehicles(email: String) =
        api.getVehicles(email)

    suspend fun addVehicle(req: VehicleRequest) =
        api.addVehicle(req)

    suspend fun deleteVehicle(plate: String) =
        api.deleteVehicle(plate)
}
