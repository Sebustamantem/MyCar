package com.example.mycar.network.dto

data class MaintenanceResponse(
    val id: Long,
    val ownerEmail: String,
    val vehiclePlate: String,
    val type: String,
    val date: String,
    val km: String?,
    val notes: String?
)
