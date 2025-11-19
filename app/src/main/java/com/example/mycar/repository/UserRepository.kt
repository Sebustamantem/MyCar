package com.example.mycar.repository

import com.example.mycar.network.RetrofitClient
import com.example.mycar.network.dto.LoginRequest
import com.example.mycar.network.dto.UserRequest
import com.example.mycar.network.dto.UserResponse

class UserRepository {

    private val api = RetrofitClient.apiUser

    suspend fun register(request: UserRequest): UserResponse =
        api.register(request)

    suspend fun login(email: String, password: String): UserResponse? =
        api.login(LoginRequest(email, password))
}
