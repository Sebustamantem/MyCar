package com.example.mycar.repository

import com.example.mycar.network.ApiClient
import com.example.mycar.network.dto.VehicleRequest
import com.example.mycar.network.dto.VehicleResponse

class VehicleRepository {

    private val api = ApiClient.vehicleService

    suspend fun getByUser(userId: Long): List<VehicleResponse> {
        return api.getByUser(userId)
    }

    suspend fun create(request: VehicleRequest): VehicleResponse {
        return api.createVehicle(request)
    }

    suspend fun update(id: Long, request: VehicleRequest) =
        api.updateVehicle(id, request)

    suspend fun delete(id: Long) {
        return api.deleteVehicle(id)
    }
}
