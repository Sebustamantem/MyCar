package com.example.mycar.network

import com.example.mycar.network.dto.ExpenseRequest
import com.example.mycar.network.dto.ExpenseResponse
import retrofit2.Response
import retrofit2.http.*

interface ExpenseService {

    @GET("api/expenses/vehicle/{vehicleId}")
    suspend fun listByVehicle(
        @Path("vehicleId") vehicleId: Long
    ): Response<List<ExpenseResponse>>

    @POST("api/expenses")
    suspend fun create(
        @Body request: ExpenseRequest
    ): Response<ExpenseResponse>

    @PUT("api/expenses/{id}")
    suspend fun update(
        @Path("id") id: Long,
        @Body request: ExpenseRequest
    ): Response<ExpenseResponse>

    @DELETE("api/expenses/{id}")
    suspend fun delete(
        @Path("id") id: Long
    ): Response<Unit> // 👈 CLAVE (204 No Content)
}
