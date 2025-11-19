package com.example.mycar.network.dto

data class UserResponse(
    val id: Long,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String
)
