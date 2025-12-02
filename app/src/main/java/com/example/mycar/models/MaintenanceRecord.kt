package com.example.mycar.models

data class MaintenanceRecord(
    val vehiclePlate: String,
    val type: String,
    val date: String,
    val km: String,
    val notes: String
)
