package com.example.mycar.repository

import com.example.mycar.network.RetrofitInstance
import com.example.mycar.network.dto.LoginRequest
import com.example.mycar.network.dto.RegisterRequest

class UserRepository {

    private val api = RetrofitInstance.api

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))

    suspend fun register(req: RegisterRequest) =
        api.register(req)
}
