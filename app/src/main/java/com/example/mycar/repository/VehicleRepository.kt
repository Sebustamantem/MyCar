package com.example.mycar.repository

import com.example.mycar.network.RetrofitClient
import com.example.mycar.network.dto.VehicleRequest
import com.example.mycar.network.dto.VehicleResponse

class VehicleRepository {

    private val api = RetrofitClient.apiVehicles

    suspend fun getVehicles(email: String): List<VehicleResponse> =
        api.getVehicles(email)

    suspend fun addVehicle(request: VehicleRequest): VehicleResponse =
        api.addVehicle(request)

    suspend fun deleteVehicle(plate: String) {
        api.deleteVehicle(plate)
    }
}
