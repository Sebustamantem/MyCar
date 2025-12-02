package com.example.mycar.network.dto

data class RegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String
)
