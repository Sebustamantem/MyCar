package com.example.mycar.network.dto

data class MaintenanceResponse(
    val id: Long,
    val type: String,
    val vehiclePlate: String,
    val date: String,
    val km: Int,
    val notes: String
)
