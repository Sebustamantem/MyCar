package com.example.mycar.network.dto

data class AlertResponse(
    val id: Long,
    val userEmail: String,
    val title: String,
    val message: String,
    val date: String
)
