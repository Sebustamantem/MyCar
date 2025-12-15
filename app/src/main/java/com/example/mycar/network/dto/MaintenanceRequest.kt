package com.example.mycar.network.dto
data class MaintenanceRequest(
    val vehicleId: Long,
    val vehiclePlate: String,
    val type: String,
    val date: String,
    val km: Int,
    val notes: String?,
    val cost: Int
)