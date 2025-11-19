package com.example.mycar.network

import com.example.mycar.network.dto.LoginRequest
import com.example.mycar.network.dto.UserRequest
import com.example.mycar.network.dto.UserResponse
import retrofit2.http.*

interface UserApiService {

    @POST("users/register")
    suspend fun register(@Body request: UserRequest): UserResponse

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): UserResponse?
}
