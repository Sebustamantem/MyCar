package com.example.mycar.model

data class MaintenanceRecord(
    val id: Long,
    val type: String,
    val vehiclePlate: String,
    val date: String,
    val km: Int,
    val notes: String
)
