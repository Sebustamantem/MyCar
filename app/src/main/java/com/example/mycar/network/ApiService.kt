package com.example.mycar.network

import com.example.mycar.network.dto.*
import retrofit2.http.*

interface ApiService {

    // ----------- REGISTRO -------------------
    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    // ----------- LOGIN ----------------------
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    // ----------- VEHÍCULOS ------------------
    @GET("vehicles/{email}")
    suspend fun getVehicles(@Path("email") email: String): List<VehicleResponse>

    @POST("vehicles/add")
    suspend fun addVehicle(@Body vehicle: VehicleRequest): VehicleResponse


    @DELETE("vehicles/{plate}")
    suspend fun deleteVehicle(@Path("plate") plate: String)
}
