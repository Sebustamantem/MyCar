package com.example.mycar.network.dto

data class MaintenanceRequest(
    val ownerEmail: String,
    val vehiclePlate: String,
    val type: String,
    val date: String,
    val km: String,
    val notes: String
)
