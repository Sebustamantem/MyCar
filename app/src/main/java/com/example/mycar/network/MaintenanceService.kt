package com.example.mycar.network

import com.example.mycar.network.dto.MaintenanceRequest
import com.example.mycar.network.dto.MaintenanceResponse
import retrofit2.http.*

interface MaintenanceService {

    @POST("maintenances")
    suspend fun createMaintenance(@Body request: MaintenanceRequest): MaintenanceResponse

    @GET("maintenances/vehicle/{vehicleId}")
    suspend fun getByVehicle(@Path("vehicleId") vehicleId: Long): List<MaintenanceResponse>

    @PUT("maintenances/{id}")
    suspend fun updateMaintenance(
        @Path("id") id: Long,
        @Body request: MaintenanceRequest
    ): MaintenanceResponse

    @DELETE("maintenances/{id}")
    suspend fun deleteMaintenance(@Path("id") id: Long)
}
