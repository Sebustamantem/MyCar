package com.example.mycar.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    // ⚠️ Si usas celular físico: pon tu IP local ej. http://192.168.1.5:8080/

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiUser: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val apiVehicles: VehicleApiService by lazy {
        retrofit.create(VehicleApiService::class.java)
    }

    val apiAlerts: AlertApiService by lazy {
        retrofit.create(AlertApiService::class.java)
    }
}

