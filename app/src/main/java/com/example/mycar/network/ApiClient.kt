package com.example.mycar.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://api-gateway-tnc0.onrender.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val vehicleService: VehicleService by lazy {
        retrofit.create(VehicleService::class.java)
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val maintenanceService: MaintenanceService by lazy {
        retrofit.create(MaintenanceService::class.java)
    }


    val expenseService: ExpenseService by lazy {
        retrofit.create(ExpenseService::class.java)
    }
}
