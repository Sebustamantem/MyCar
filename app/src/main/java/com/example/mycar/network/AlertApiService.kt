package com.example.mycar.network

import com.example.mycar.network.dto.AlertRequest
import com.example.mycar.network.dto.AlertResponse
import retrofit2.http.*

interface AlertApiService {

    @GET("alerts/{email}")
    suspend fun getAlerts(@Path("email") email: String): List<AlertResponse>

    @POST("alerts")
    suspend fun addAlert(@Body request: AlertRequest): AlertResponse

    @DELETE("alerts/{id}")
    suspend fun deleteAlert(@Path("id") id: Long)
}
