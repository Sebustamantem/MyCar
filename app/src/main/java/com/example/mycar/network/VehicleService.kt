package com.example.mycar.network

import com.example.mycar.network.dto.VehicleRequest
import com.example.mycar.network.dto.VehicleResponse
import retrofit2.http.*

interface VehicleService {

    @GET("vehicles/user/{userId}")
    suspend fun getByUser(@Path("userId") userId: Long): List<VehicleResponse>

    @POST("vehicles")
    suspend fun createVehicle(@Body request: VehicleRequest): VehicleResponse

    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: Long)
}
