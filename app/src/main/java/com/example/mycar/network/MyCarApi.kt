package com.example.mycar.network

import com.example.mycar.network.dto.*
import retrofit2.http.*

interface MyCarApi {

    // AUTH
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): UserResponse

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): UserResponse

    // VEHICLES
    @GET("vehicles/{email}")
    suspend fun getVehicles(@Path("email") email: String): List<VehicleResponse>

    @POST("vehicles")
    suspend fun addVehicle(@Body req: VehicleRequest): VehicleResponse

    @DELETE("vehicles/{plate}")
    suspend fun deleteVehicle(@Path("plate") plate: String)

    // ALERTS
    @GET("alerts/{email}")
    suspend fun getAlerts(@Path("email") email: String): List<AlertResponse>

    @POST("alerts")
    suspend fun addAlert(@Body req: AlertRequest): AlertResponse

    @DELETE("alerts/{id}")
    suspend fun deleteAlert(@Path("id") id: Long)

    // MAINTENANCE
    @GET("maintenance/{email}")
    suspend fun getMaintenance(@Path("email") email: String): List<MaintenanceResponse>

    @POST("maintenance")
    suspend fun addMaintenance(@Body req: MaintenanceRequest): MaintenanceResponse

    @DELETE("maintenance/{id}")
    suspend fun deleteMaintenance(@Path("id") id: Long)
}
