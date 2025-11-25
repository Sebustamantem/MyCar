package com.example.mycar.network.dto

data class MaintenanceRequest(
    val type: String,
    val vehiclePlate: String,
    val date: String,
    val km: Int,
    val notes: String
)
