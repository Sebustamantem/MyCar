package com.example.mycar.repository

import com.example.mycar.network.ApiClient
import com.example.mycar.network.AuthService
import com.example.mycar.network.dto.*

class AuthRepository {

    private val api = ApiClient.retrofit.create(AuthService::class.java)

    // LOGIN
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTER
    suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        phone: String
    ): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, lastName, email, password, phone))

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
