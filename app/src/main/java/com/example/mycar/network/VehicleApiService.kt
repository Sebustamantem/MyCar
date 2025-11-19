package com.example.mycar.network

import com.example.mycar.network.dto.VehicleRequest
import com.example.mycar.network.dto.VehicleResponse
import retrofit2.http.*

interface VehicleApiService {

    @GET("vehicles/{email}")
    suspend fun getVehicles(@Path("email") email: String): List<VehicleResponse>

    @POST("vehicles")
    suspend fun addVehicle(@Body request: VehicleRequest): VehicleResponse

    @DELETE("vehicles/{plate}")
    suspend fun deleteVehicle(@Path("plate") plate: String)
}
