package com.example.mycar.network.dto

data class LoginResponse(
    val id: Long?,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val token: String
)